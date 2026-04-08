package edu.hotel.booking.dto.room;

import edu.hotel.booking.model.RoomStatus;
import lombok.Data;

@Data
public class RoomResponse {

    private Long id;

    private Integer roomNumber;

    private Integer floor;

    private RoomStatus status;
}
