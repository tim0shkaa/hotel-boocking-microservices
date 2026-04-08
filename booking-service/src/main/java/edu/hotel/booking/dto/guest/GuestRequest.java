package edu.hotel.booking.dto.guest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GuestRequest {

    @NotBlank(message = "Имя не должно быть пустым")
    private String firstName;

    @NotBlank(message = "Фамилия не должна быть пустой")
    private String surname;

    private String patronymic;

    @NotNull(message = "Дата рождения не может быть пустой")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthDate;

    @NotBlank(message = "Номер телефона не может быть пустым")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Некорректный формат номера телефона")
    private String phoneNumber;
}
