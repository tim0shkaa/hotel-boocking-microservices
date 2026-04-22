package edu.hotel.payment.service;

import edu.hotel.common.exception.AccessDeniedException;
import edu.hotel.common.exception.NotFoundException;
import edu.hotel.payment.dto.refund.RefundResponse;
import edu.hotel.payment.entity.Payment;
import edu.hotel.payment.entity.Refund;
import edu.hotel.payment.exception.InvalidPaymentStatusException;
import edu.hotel.payment.exception.InvalidRefundAmountException;
import edu.hotel.payment.exception.InvalidRefundStatusException;
import edu.hotel.payment.mapper.RefundMapper;
import edu.hotel.payment.model.PaymentStatus;
import edu.hotel.payment.model.RefundStatus;
import edu.hotel.payment.provider.MockPaymentProvider;
import edu.hotel.payment.repository.PaymentRepository;
import edu.hotel.payment.repository.RefundRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundServiceImplTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private RefundRepository refundRepository;
    @Mock private MockPaymentProvider mockPaymentProvider;
    @Mock private RefundMapper refundMapper;

    @InjectMocks
    private RefundServiceImpl refundService;

    @Test
    void requestRefund_shouldCreateRefundAndCallProvider() {
        Long paymentId = 1L;

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setStatus(PaymentStatus.CONFIRMED);
        payment.setAmount(new BigDecimal("1000.00"));

        Refund savedRefund = new Refund();
        savedRefund.setId(10L);

        RefundResponse response = new RefundResponse();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(refundRepository.findAllByPaymentId(paymentId)).thenReturn(List.of());
        when(refundRepository.save(any())).thenReturn(savedRefund);
        when(refundMapper.toResponse(any())).thenReturn(response);

        RefundResponse result = refundService.requestRefund(paymentId, new BigDecimal("500.00"), "Отмена");

        assertThat(result).isEqualTo(response);

        ArgumentCaptor<Refund> captor = ArgumentCaptor.forClass(Refund.class);
        verify(refundRepository).save(captor.capture());
        assertThat(captor.getValue().getAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(captor.getValue().getStatus()).isEqualTo(RefundStatus.PENDING);
        assertThat(captor.getValue().getPayment()).isEqualTo(payment);
        assertThat(captor.getValue().getReason()).isEqualTo("Отмена");

        verify(mockPaymentProvider).processRefund(eq(10L));
    }

    @Test
    void requestRefund_shouldThrowNotFoundException_whenPaymentNotFound() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refundService.requestRefund(99L, BigDecimal.TEN, "reason"))
                .isInstanceOf(NotFoundException.class);

        verify(refundRepository, never()).save(any());
    }

    @Test
    void requestRefund_shouldThrowInvalidPaymentStatusException_whenStatusNotConfirmed() {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> refundService.requestRefund(1L, BigDecimal.TEN, "reason"))
                .isInstanceOf(InvalidPaymentStatusException.class);

        verify(refundRepository, never()).save(any());
    }

    @Test
    void requestRefund_shouldThrowInvalidRefundAmountException_whenAmountExceedsAvailable() {
        Long paymentId = 1L;

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setStatus(PaymentStatus.CONFIRMED);
        payment.setAmount(new BigDecimal("1000.00"));

        Refund existingRefund = new Refund();
        existingRefund.setAmount(new BigDecimal("600.00"));

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(refundRepository.findAllByPaymentId(paymentId)).thenReturn(List.of(existingRefund));

        assertThatThrownBy(() -> refundService.requestRefund(paymentId, new BigDecimal("500.00"), "reason"))
                .isInstanceOf(InvalidRefundAmountException.class);

        verify(refundRepository, never()).save(any());
    }

    @Test
    void retryRefund_shouldSetProcessingAndCallProvider() {
        Long refundId = 1L;
        Long userId = 10L;

        Payment payment = new Payment();
        payment.setUserId(userId);

        Refund refund = new Refund();
        refund.setId(refundId);
        refund.setPayment(payment);
        refund.setStatus(RefundStatus.FAILED);

        RefundResponse response = new RefundResponse();

        when(refundRepository.findWithPaymentById(refundId)).thenReturn(Optional.of(refund));
        when(refundMapper.toResponse(refund)).thenReturn(response);

        RefundResponse result = refundService.retryRefund(refundId, userId, "ROLE_GUEST");

        assertThat(result).isEqualTo(response);
        assertThat(refund.getStatus()).isEqualTo(RefundStatus.PROCESSING);
        verify(mockPaymentProvider).processRefund(eq(refundId));
    }

    @Test
    void retryRefund_shouldThrowAccessDeniedException_whenGuestRetriesAnotherRefund() {
        Long refundId = 1L;

        Payment payment = new Payment();
        payment.setUserId(99L);

        Refund refund = new Refund();
        refund.setId(refundId);
        refund.setPayment(payment);
        refund.setStatus(RefundStatus.FAILED);

        when(refundRepository.findWithPaymentById(refundId)).thenReturn(Optional.of(refund));

        assertThatThrownBy(() -> refundService.retryRefund(refundId, 10L, "ROLE_GUEST"))
                .isInstanceOf(AccessDeniedException.class);

        verify(mockPaymentProvider, never()).processRefund(any());
    }

    @Test
    void retryRefund_shouldThrowInvalidRefundStatusException_whenStatusNotFailed() {
        Long refundId = 1L;
        Long userId = 10L;

        Payment payment = new Payment();
        payment.setUserId(userId);

        Refund refund = new Refund();
        refund.setId(refundId);
        refund.setPayment(payment);
        refund.setStatus(RefundStatus.PENDING);

        when(refundRepository.findWithPaymentById(refundId)).thenReturn(Optional.of(refund));

        assertThatThrownBy(() -> refundService.retryRefund(refundId, userId, "ROLE_GUEST"))
                .isInstanceOf(InvalidRefundStatusException.class);

        verify(mockPaymentProvider, never()).processRefund(any());
    }

    @Test
    void retryRefund_shouldThrowNotFoundException_whenRefundNotFound() {
        when(refundRepository.findWithPaymentById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refundService.retryRefund(99L, 1L, "ROLE_GUEST"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getRefundById_shouldReturnResponse_whenGuestAccessesOwnRefund() {
        Long refundId = 1L;
        Long userId = 10L;

        Payment payment = new Payment();
        payment.setUserId(userId);

        Refund refund = new Refund();
        refund.setPayment(payment);

        RefundResponse response = new RefundResponse();

        when(refundRepository.findWithPaymentById(refundId)).thenReturn(Optional.of(refund));
        when(refundMapper.toResponse(refund)).thenReturn(response);

        RefundResponse result = refundService.getRefundById(refundId, userId, "ROLE_GUEST");

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getRefundById_shouldThrowAccessDeniedException_whenGuestAccessesAnotherRefund() {
        Long refundId = 1L;

        Payment payment = new Payment();
        payment.setUserId(99L);

        Refund refund = new Refund();
        refund.setPayment(payment);

        when(refundRepository.findWithPaymentById(refundId)).thenReturn(Optional.of(refund));

        assertThatThrownBy(() -> refundService.getRefundById(refundId, 10L, "ROLE_GUEST"))
                .isInstanceOf(AccessDeniedException.class);

        verify(refundMapper, never()).toResponse(any());
    }

    @Test
    void getRefundById_shouldThrowNotFoundException_whenRefundNotFound() {
        when(refundRepository.findWithPaymentById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refundService.getRefundById(99L, 1L, "ROLE_GUEST"))
                .isInstanceOf(NotFoundException.class);
    }
}