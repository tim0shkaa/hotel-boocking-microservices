package edu.hotel.review.service;

import edu.hotel.common.exception.AlreadyExistsException;
import edu.hotel.common.exception.NotFoundException;
import edu.hotel.events.ReviewCreatedEvent;
import edu.hotel.review.dto.review.ReviewRequest;
import edu.hotel.review.dto.review.ReviewResponse;
import edu.hotel.review.entity.EligibleBooking;
import edu.hotel.review.entity.Review;
import edu.hotel.review.exception.BookingNotEligibleForReviewException;
import edu.hotel.review.kafka.ReviewEventProducer;
import edu.hotel.review.mapper.ReviewMapper;
import edu.hotel.review.repository.EligibleBookingRepository;
import edu.hotel.review.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock private EligibleBookingRepository eligibleBookingRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private ReviewMapper reviewMapper;
    @Mock private ReviewEventProducer reviewEventProducer;
    @Mock private RatingAggregateService ratingAggregateService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void createReview_shouldCreateReviewAndSendEvent() {
        Long userId = 10L;
        Long bookingId = 1L;

        ReviewRequest request = new ReviewRequest();
        request.setBookingId(bookingId);

        EligibleBooking eligibleBooking = new EligibleBooking();
        eligibleBooking.setUserId(userId);
        eligibleBooking.setHotelId(100L);
        eligibleBooking.setGuestId(5L);
        eligibleBooking.setRoomTypeId(20L);

        Review reviewFromMapper = new Review();
        Review savedReview = new Review();
        savedReview.setId(99L);
        savedReview.setHotelId(100L);
        savedReview.setGuestId(5L);
        savedReview.setRoomTypeId(20L);
        savedReview.setOverallRating(5);

        ReviewResponse response = new ReviewResponse();

        when(eligibleBookingRepository.findByBookingId(bookingId)).thenReturn(Optional.of(eligibleBooking));
        when(reviewRepository.existsByBookingId(bookingId)).thenReturn(false);
        when(reviewMapper.toEntity(request)).thenReturn(reviewFromMapper);
        when(reviewRepository.save(reviewFromMapper)).thenReturn(savedReview);
        when(reviewMapper.toResponse(savedReview)).thenReturn(response);

        ReviewResponse result = reviewService.createReview(request, userId);

        assertThat(result).isEqualTo(response);
        assertThat(reviewFromMapper.getHotelId()).isEqualTo(100L);
        assertThat(reviewFromMapper.getGuestId()).isEqualTo(5L);
        assertThat(reviewFromMapper.getRoomTypeId()).isEqualTo(20L);

        verify(ratingAggregateService).updateRating(savedReview);

        ArgumentCaptor<ReviewCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ReviewCreatedEvent.class);
        verify(reviewEventProducer).sendReviewCreated(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getReviewId()).isEqualTo(99L);
        assertThat(eventCaptor.getValue().getHotelId()).isEqualTo(100L);
    }

    @Test
    void createReview_shouldThrowNotFoundException_whenEligibleBookingNotFound() {
        ReviewRequest request = new ReviewRequest();
        request.setBookingId(1L);

        when(eligibleBookingRepository.findByBookingId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(request, 10L))
                .isInstanceOf(NotFoundException.class);

        verify(reviewRepository, never()).save(any());
        verify(reviewEventProducer, never()).sendReviewCreated(any());
    }

    @Test
    void createReview_shouldThrowBookingNotEligibleForReviewException_whenUserIdNotMatch() {
        Long bookingId = 1L;

        ReviewRequest request = new ReviewRequest();
        request.setBookingId(bookingId);

        EligibleBooking eligibleBooking = new EligibleBooking();
        eligibleBooking.setUserId(99L);

        when(eligibleBookingRepository.findByBookingId(bookingId)).thenReturn(Optional.of(eligibleBooking));

        assertThatThrownBy(() -> reviewService.createReview(request, 10L))
                .isInstanceOf(BookingNotEligibleForReviewException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReview_shouldThrowAlreadyExistsException_whenReviewAlreadyExists() {
        Long bookingId = 1L;
        Long userId = 10L;

        ReviewRequest request = new ReviewRequest();
        request.setBookingId(bookingId);

        EligibleBooking eligibleBooking = new EligibleBooking();
        eligibleBooking.setUserId(userId);

        when(eligibleBookingRepository.findByBookingId(bookingId)).thenReturn(Optional.of(eligibleBooking));
        when(reviewRepository.existsByBookingId(bookingId)).thenReturn(true);

        assertThatThrownBy(() -> reviewService.createReview(request, userId))
                .isInstanceOf(AlreadyExistsException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void findById_shouldReturnResponse_whenReviewExists() {
        Long reviewId = 1L;
        Review review = new Review();
        ReviewResponse response = new ReviewResponse();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewMapper.toResponse(review)).thenReturn(response);

        ReviewResponse result = reviewService.findById(reviewId);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void findById_shouldThrowNotFoundException_whenReviewNotFound() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.findById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findByHotelId_shouldReturnPage() {
        Long hotelId = 1L;
        Pageable pageable = Pageable.unpaged();
        Review review = new Review();
        ReviewResponse response = new ReviewResponse();

        when(reviewRepository.findAllByHotelId(hotelId, pageable)).thenReturn(new PageImpl<>(List.of(review)));
        when(reviewMapper.toResponse(review)).thenReturn(response);

        Page<ReviewResponse> result = reviewService.findByHotelId(hotelId, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(response);
    }

    @Test
    void findByRoomTypeId_shouldReturnPage() {
        Long roomTypeId = 1L;
        Pageable pageable = Pageable.unpaged();
        Review review = new Review();
        ReviewResponse response = new ReviewResponse();

        when(reviewRepository.findAllByRoomTypeId(roomTypeId, pageable)).thenReturn(new PageImpl<>(List.of(review)));
        when(reviewMapper.toResponse(review)).thenReturn(response);

        Page<ReviewResponse> result = reviewService.findByRoomTypeId(roomTypeId, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(response);
    }

    @Test
    void delete_shouldDeleteReview_whenReviewExists() {
        Long reviewId = 1L;
        Review review = new Review();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        reviewService.delete(reviewId);

        verify(reviewRepository).delete(review);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenReviewNotFound() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.delete(99L))
                .isInstanceOf(NotFoundException.class);

        verify(reviewRepository, never()).delete(any());
    }
}