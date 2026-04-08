package edu.hotel.booking.dto.tariff;

import edu.hotel.booking.model.Currency;
import edu.hotel.booking.validation.DateRangeValidatable;
import edu.hotel.booking.validation.ValidDateRange;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ValidDateRange
public class TariffRequest implements DateRangeValidatable {

    @NotBlank(message = "Название тарифа обязательно")
    @Size(max = 255, message = "Название не должно превышать 255 символов")
    private String name;

    @NotNull(message = "Цена за ночь обязательна")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше нуля")
    @DecimalMax(value = "1000000.00", message = "Цена не должна превышать 1 000 000")
    private BigDecimal pricePerNight;

    @NotNull(message = "Валюта обязательна")
    private Currency currency;

    @NotNull(message = "Дата начала действия обязательна")
    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    private LocalDate validFrom;

    @NotNull(message = "Дата окончания действия обязательна")
    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDate validTo;

    @Size(max = 2000, message = "Условия не должны превышать 2000 символов")
    private String conditions;


    @Override
    public LocalDate getDateFrom() {
        return getValidFrom();
    }

    @Override
    public LocalDate getDateTo() {
        return getValidTo();
    }
}