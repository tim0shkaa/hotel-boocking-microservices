package edu.hotel.booking.dto.roomtype;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class RoomTypeRequest {

    @NotBlank(message = "Название типа номера обязательно")
    @Size(max = 255, message = "Название не должно превышать 255 символов")
    private String name;

    @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
    private String description;

    @NotNull(message = "Вместимость обязательна")
    @Min(value = 1, message = "Вместимость должна быть не менее 1")
    @Max(value = 10, message = "Вместимость не должна превышать 10 человек")
    private Integer capacity;

    private List<String> amenities;

    private List<String> photos;
}
