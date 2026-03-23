package edu.hotel.booking.dto.room;

import edu.hotel.booking.model.RoomStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomStatusRequest {

    @NotNull(message = "Статус номера обязателен")
    private RoomStatus status;
}
