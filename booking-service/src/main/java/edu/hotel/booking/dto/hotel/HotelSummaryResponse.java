package edu.hotel.booking.dto.hotel;

import lombok.Data;

import java.util.List;

@Data
public class HotelSummaryResponse {

    private Long id;

    private String name;

    private String address;

    private String city;

    private Integer starRating;

    private List<String> amenities;

    private Double avgRating;
}
