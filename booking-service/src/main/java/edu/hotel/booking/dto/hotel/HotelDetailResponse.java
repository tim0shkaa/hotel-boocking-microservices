package edu.hotel.booking.dto.hotel;

import edu.hotel.booking.dto.roomtype.RoomTypeResponse;
import lombok.Data;

import java.util.List;

@Data
public class HotelDetailResponse {

    private Long id;

    private String name;

    private String address;

    private String city;

    private Integer starRating;

    private String description;

    private List<String> amenities;

    private Double avgRating;

    private List<RoomTypeResponse> roomTypes;
}
