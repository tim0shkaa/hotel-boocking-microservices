package edu.hotel.booking.dto.booking;

import edu.hotel.booking.validation.DateRangeValidatable;
import edu.hotel.booking.validation.ValidDateRange;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@ValidDateRange
public class BookingCreateRequest implements DateRangeValidatable {

    @NotNull(message = "Тип номера обязателен")
    private Long roomTypeId;

    @NotNull(message = "Тариф обязателен")
    private Long tariffId;

    @NotNull(message = "Дата заезда обязательна")
    @FutureOrPresent(message = "Дата заезда не может быть в прошлом")
    private LocalDate checkIn;

    @NotNull(message = "Дата выезда обязательна")
    @Future(message = "Дата выезда должна быть в будущем")
    private LocalDate checkOut;

    @Size(max = 2000, message = "Примечания не должны превышать 2000 символов")
    private String notes;

    @Override
    public LocalDate getDateFrom() { return checkIn; }

    @Override
    public LocalDate getDateTo() { return checkOut; }
}
