package edu.hotel.payment.service;

import edu.hotel.common.exception.AccessDeniedException;
import edu.hotel.common.exception.NotFoundException;
import edu.hotel.payment.dto.payment.PaymentRequest;
import edu.hotel.payment.dto.payment.PaymentResponse;
import edu.hotel.payment.entity.Payment;
import edu.hotel.payment.entity.PaymentAttempt;
import edu.hotel.payment.exception.InvalidPaymentStatusException;
import edu.hotel.payment.mapper.PaymentMapper;
import edu.hotel.payment.model.AttemptStatus;
import edu.hotel.payment.model.PaymentStatus;
import edu.hotel.payment.provider.MockPaymentProvider;
import edu.hotel.payment.repository.PaymentAttemptRepository;
import edu.hotel.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private PaymentAttemptRepository paymentAttemptRepository;
    @Mock private MockPaymentProvider mockPaymentProvider;
    @Mock private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void initiatePayment_shouldDoNothing_whenPaymentAlreadyExists() {
        when(paymentRepository.findByBookingId(1L)).thenReturn(Optional.of(new Payment()));

        paymentService.initiatePayment(1L, 2L, 3L, BigDecimal.TEN, "RUB");

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void initiatePayment_shouldCreatePayment_whenPaymentNotExists() {
        ReflectionTestUtils.setField(paymentService, "mockProvider", "MOCK");

        when(paymentRepository.findByBookingId(1L)).thenReturn(Optional.empty());

        paymentService.initiatePayment(1L, 2L, 3L, BigDecimal.TEN, "RUB");

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        Payment saved = captor.getValue();
        assertThat(saved.getBookingId()).isEqualTo(1L);
        assertThat(saved.getGuestId()).isEqualTo(2L);
        assertThat(saved.getUserId()).isEqualTo(3L);
        assertThat(saved.getAmount()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(saved.getCurrency()).isEqualTo("RUB");
        assertThat(saved.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(saved.getProvider()).isEqualTo("MOCK");
    }

    @Test
    void processPayment_shouldCreateAttemptAndCallProvider() {
        Long bookingId = 1L;
        PaymentRequest request = new PaymentRequest();
        request.setBookingId(bookingId);

        Payment payment = new Payment();
        payment.setId(10L);
        payment.setStatus(PaymentStatus.PENDING);

        PaymentAttempt savedAttempt = new PaymentAttempt();
        savedAttempt.setId(20L);

        PaymentResponse response = new PaymentResponse();

        when(paymentRepository.findByBookingId(bookingId)).thenReturn(Optional.of(payment));
        when(paymentAttemptRepository.save(any())).thenReturn(savedAttempt);
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        PaymentResponse result = paymentService.processPayment(request);

        assertThat(result).isEqualTo(response);

        ArgumentCaptor<PaymentAttempt> captor = ArgumentCaptor.forClass(PaymentAttempt.class);
        verify(paymentAttemptRepository).save(captor.capture());
        assertThat(captor.getValue().getAttemptNumber()).isEqualTo(1);
        assertThat(captor.getValue().getStatus()).isEqualTo(AttemptStatus.IN_PROGRESS);
        assertThat(captor.getValue().getPayment()).isEqualTo(payment);

        verify(mockPaymentProvider).processPayment(eq(10L), eq(20L));
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PROCESSING);
    }

    @Test
    void processPayment_shouldThrowNotFoundException_whenPaymentNotFound() {
        PaymentRequest request = new PaymentRequest();
        request.setBookingId(99L);

        when(paymentRepository.findByBookingId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.processPayment(request))
                .isInstanceOf(NotFoundException.class);

        verify(paymentAttemptRepository, never()).save(any());
        verify(mockPaymentProvider, never()).processPayment(any(), any());
    }

    @Test
    void processPayment_shouldThrowInvalidPaymentStatusException_whenStatusNotPending() {
        PaymentRequest request = new PaymentRequest();
        request.setBookingId(1L);

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.CONFIRMED);

        when(paymentRepository.findByBookingId(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.processPayment(request))
                .isInstanceOf(InvalidPaymentStatusException.class);

        verify(paymentAttemptRepository, never()).save(any());
    }

    @Test
    void retryPayment_shouldIncrementAttemptNumberAndCallProvider() {
        Long paymentId = 1L;
        Long userId = 10L;

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setUserId(userId);
        payment.setStatus(PaymentStatus.FAILED);

        PaymentAttempt lastAttempt = new PaymentAttempt();
        lastAttempt.setAttemptNumber(2);

        PaymentAttempt savedAttempt = new PaymentAttempt();
        savedAttempt.setId(30L);

        PaymentResponse response = new PaymentResponse();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentAttemptRepository.findTopByPaymentIdOrderByAttemptNumberDesc(paymentId))
                .thenReturn(Optional.of(lastAttempt));
        when(paymentAttemptRepository.save(any())).thenReturn(savedAttempt);
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        PaymentResponse result = paymentService.retryPayment(paymentId, userId, "ROLE_GUEST");

        assertThat(result).isEqualTo(response);

        ArgumentCaptor<PaymentAttempt> captor = ArgumentCaptor.forClass(PaymentAttempt.class);
        verify(paymentAttemptRepository).save(captor.capture());
        assertThat(captor.getValue().getAttemptNumber()).isEqualTo(3);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PROCESSING);
        verify(mockPaymentProvider).processPayment(eq(paymentId), eq(30L));
    }

    @Test
    void retryPayment_shouldThrowAccessDeniedException_whenGuestRetriesAnotherPayment() {
        Long paymentId = 1L;

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setUserId(99L);
        payment.setStatus(PaymentStatus.FAILED);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.retryPayment(paymentId, 10L, "ROLE_GUEST"))
                .isInstanceOf(AccessDeniedException.class);

        verify(paymentAttemptRepository, never()).save(any());
    }

    @Test
    void retryPayment_shouldThrowInvalidPaymentStatusException_whenStatusNotFailed() {
        Long paymentId = 1L;
        Long userId = 10L;

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setUserId(userId);
        payment.setStatus(PaymentStatus.CONFIRMED);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.retryPayment(paymentId, userId, "ROLE_GUEST"))
                .isInstanceOf(InvalidPaymentStatusException.class);

        verify(paymentAttemptRepository, never()).save(any());
    }

    @Test
    void retryPayment_shouldThrowNotFoundException_whenPaymentNotFound() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.retryPayment(99L, 1L, "ROLE_GUEST"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findPaymentByPaymentId_shouldReturnResponse_whenGuestAccessesOwnPayment() {
        Long paymentId = 1L;
        Long userId = 10L;

        Payment payment = new Payment();
        payment.setUserId(userId);

        PaymentResponse response = new PaymentResponse();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        PaymentResponse result = paymentService.findPaymentByPaymentId(paymentId, userId, "ROLE_GUEST");

        assertThat(result).isEqualTo(response);
    }

    @Test
    void findPaymentByPaymentId_shouldThrowAccessDeniedException_whenGuestAccessesAnotherPayment() {
        Long paymentId = 1L;

        Payment payment = new Payment();
        payment.setUserId(99L);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.findPaymentByPaymentId(paymentId, 10L, "ROLE_GUEST"))
                .isInstanceOf(AccessDeniedException.class);

        verify(paymentMapper, never()).toResponse(any());
    }

    @Test
    void findPaymentByPaymentId_shouldThrowNotFoundException_whenPaymentNotFound() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.findPaymentByPaymentId(99L, 1L, "ROLE_GUEST"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findPaymentByBookingId_shouldReturnResponse_whenGuestAccessesOwnPayment() {
        Long bookingId = 1L;
        Long userId = 10L;

        Payment payment = new Payment();
        payment.setUserId(userId);

        PaymentResponse response = new PaymentResponse();

        when(paymentRepository.findByBookingId(bookingId)).thenReturn(Optional.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        PaymentResponse result = paymentService.findPaymentByBookingId(bookingId, userId, "ROLE_GUEST");

        assertThat(result).isEqualTo(response);
    }

    @Test
    void findPaymentByBookingId_shouldThrowAccessDeniedException_whenGuestAccessesAnotherPayment() {
        Long bookingId = 1L;

        Payment payment = new Payment();
        payment.setUserId(99L);

        when(paymentRepository.findByBookingId(bookingId)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.findPaymentByBookingId(bookingId, 10L, "ROLE_GUEST"))
                .isInstanceOf(AccessDeniedException.class);

        verify(paymentMapper, never()).toResponse(any());
    }

    @Test
    void findPaymentByBookingId_shouldThrowNotFoundException_whenPaymentNotFound() {
        when(paymentRepository.findByBookingId(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.findPaymentByBookingId(99L, 1L, "ROLE_GUEST"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAllPayments_shouldFilterByGuestId_whenGuestIdProvided() {
        Long guestId = 5L;
        Pageable pageable = Pageable.unpaged();
        Payment payment = new Payment();
        PaymentResponse response = new PaymentResponse();
        Page<Payment> page = new PageImpl<>(List.of(payment));

        when(paymentRepository.findAllByGuestId(guestId, pageable)).thenReturn(page);
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        Page<PaymentResponse> result = paymentService.getAllPayments(guestId, pageable);

        assertThat(result).hasSize(1);
        verify(paymentRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllPayments_shouldReturnAll_whenGuestIdIsNull() {
        Pageable pageable = Pageable.unpaged();
        Page<Payment> page = new PageImpl<>(List.of());

        when(paymentRepository.findAll(pageable)).thenReturn(page);

        Page<PaymentResponse> result = paymentService.getAllPayments(null, pageable);

        assertThat(result).isEmpty();
        verify(paymentRepository, never()).findAllByGuestId(any(), any());
    }
}