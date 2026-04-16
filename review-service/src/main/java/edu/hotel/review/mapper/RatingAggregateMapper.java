package edu.hotel.review.mapper;

import edu.hotel.review.dto.rating.RatingAggregateResponse;
import edu.hotel.review.entity.RatingAggregate;

public interface RatingAggregateMapper {

    RatingAggregateResponse toResponse(RatingAggregate ratingAggregate);
}
