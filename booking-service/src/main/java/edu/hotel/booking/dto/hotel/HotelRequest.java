package edu.hotel.booking.dto.hotel;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class HotelRequest {

    @NotBlank(message = "Название отеля обязательно")
    @Size(max = 255, message = "Название не должно превышать 255 символов")
    private String name;

    @NotBlank(message = "Адрес обязателен")
    @Size(max = 255, message = "Адрес не должен превышать 255 символов")
    private String address;

    @NotBlank(message = "Город обязателен")
    @Size(max = 255, message = "Название города не должно превышать 255 символов")
    private String city;

    @NotNull(message = "Рейтинг звёзд обязателен")
    @Min(value = 1, message = "Минимальный рейтинг — 1 звезда")
    @Max(value = 5, message = "Максимальный рейтинг — 5 звёзд")
    private Integer starRating;

    @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
    private String description;

    private List<String> amenities;
}
