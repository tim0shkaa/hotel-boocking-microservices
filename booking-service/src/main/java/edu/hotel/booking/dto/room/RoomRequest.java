package edu.hotel.booking.dto.room;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomRequest {

    @NotNull(message = "Номер комнаты обязателен")
    @Min(value = 1, message = "Номер комнаты должен быть положительным")
    private Integer roomNumber;

    @NotNull(message = "Этаж обязателен")
    private Integer floor;
}
