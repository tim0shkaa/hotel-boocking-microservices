package edu.hotel.review.service;

import edu.hotel.review.dto.rating.RatingAggregateResponse;
import edu.hotel.review.entity.RatingAggregate;
import edu.hotel.review.entity.Review;
import edu.hotel.review.mapper.RatingAggregateMapper;
import edu.hotel.review.model.TargetType;
import edu.hotel.review.repository.RatingAggregateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingAggregateServiceImplTest {

    @Mock private RatingAggregateRepository ratingAggregateRepository;
    @Mock private RatingAggregateMapper ratingAggregateMapper;

    @InjectMocks
    private RatingAggregateServiceImpl ratingAggregateService;

    @Test
    void updateRating_shouldRecalculateAvgAndIncrementTotal_whenAggregateExists() {
        Review review = new Review();
        review.setHotelId(1L);
        review.setRoomTypeId(2L);
        review.setOverallRating(4);

        RatingAggregate hotelAggregate = new RatingAggregate();
        hotelAggregate.setTargetType(TargetType.HOTEL);
        hotelAggregate.setTargetId(1L);
        hotelAggregate.setTotalReviews(4);
        hotelAggregate.setAvgRating(3.0);

        RatingAggregate roomTypeAggregate = new RatingAggregate();
        roomTypeAggregate.setTargetType(TargetType.ROOM_TYPE);
        roomTypeAggregate.setTargetId(2L);
        roomTypeAggregate.setTotalReviews(2);
        roomTypeAggregate.setAvgRating(5.0);

        when(ratingAggregateRepository.findByTargetTypeAndTargetId(TargetType.HOTEL, 1L))
                .thenReturn(Optional.of(hotelAggregate));
        when(ratingAggregateRepository.findByTargetTypeAndTargetId(TargetType.ROOM_TYPE, 1L))
                .thenReturn(Optional.of(roomTypeAggregate));

        ratingAggregateService.updateRating(review);

        assertThat(hotelAggregate.getTotalReviews()).isEqualTo(5);
        assertThat(hotelAggregate.getAvgRating()).isEqualTo((3.0 * 4 + 4) / 5);

        assertThat(roomTypeAggregate.getTotalReviews()).isEqualTo(3);
        assertThat(roomTypeAggregate.getAvgRating()).isEqualTo((5.0 * 2 + 4) / 3);

        verify(ratingAggregateRepository, times(2)).save(any());
    }

    @Test
    void updateRating_shouldCreateNewAggregate_whenAggregateNotExists() {
        Review review = new Review();
        review.setHotelId(1L);
        review.setRoomTypeId(2L);
        review.setOverallRating(5);

        when(ratingAggregateRepository.findByTargetTypeAndTargetId(any(), any()))
                .thenReturn(Optional.empty());

        ratingAggregateService.updateRating(review);

        ArgumentCaptor<RatingAggregate> captor = ArgumentCaptor.forClass(RatingAggregate.class);
        verify(ratingAggregateRepository, times(2)).save(captor.capture());

        List<RatingAggregate> saved = captor.getAllValues();
        assertThat(saved).allSatisfy(aggregate -> {
            assertThat(aggregate.getTotalReviews()).isEqualTo(1);
            assertThat(aggregate.getAvgRating()).isEqualTo(5.0);
        });
    }

    @Test
    void ratingByHotelId_shouldReturnResponse_whenAggregateExists() {
        Long hotelId = 1L;
        RatingAggregate aggregate = new RatingAggregate();
        RatingAggregateResponse response = new RatingAggregateResponse();

        when(ratingAggregateRepository.findByTargetTypeAndTargetId(TargetType.HOTEL, hotelId))
                .thenReturn(Optional.of(aggregate));
        when(ratingAggregateMapper.toResponse(aggregate)).thenReturn(response);

        RatingAggregateResponse result = ratingAggregateService.ratingByHotelId(hotelId);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void ratingByHotelId_shouldReturnDefaultResponse_whenAggregateNotExists() {
        Long hotelId = 1L;
        RatingAggregateResponse response = new RatingAggregateResponse();

        when(ratingAggregateRepository.findByTargetTypeAndTargetId(TargetType.HOTEL, hotelId))
                .thenReturn(Optional.empty());
        when(ratingAggregateMapper.toResponse(any())).thenReturn(response);

        RatingAggregateResponse result = ratingAggregateService.ratingByHotelId(hotelId);

        assertThat(result).isEqualTo(response);

        ArgumentCaptor<RatingAggregate> captor = ArgumentCaptor.forClass(RatingAggregate.class);
        verify(ratingAggregateMapper).toResponse(captor.capture());
        assertThat(captor.getValue().getTotalReviews()).isEqualTo(0);
        assertThat(captor.getValue().getAvgRating()).isEqualTo(0.0);
        assertThat(captor.getValue().getTargetType()).isEqualTo(TargetType.HOTEL);
        assertThat(captor.getValue().getTargetId()).isEqualTo(hotelId);
    }
}