package edu.hotel.booking.dto.room;

import lombok.Data;

@Data
public class RoomSummaryResponse {

    private Long id;

    private Integer roomNumber;

    private Integer floor;

    private String roomTypeName;

    private String hotelName;

    private String hotelCity;
}
