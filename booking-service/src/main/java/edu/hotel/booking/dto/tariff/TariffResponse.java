package edu.hotel.booking.dto.tariff;

import edu.hotel.booking.dto.roomtype.RoomTypeResponse;
import edu.hotel.booking.model.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TariffResponse {

    private Long id;

    private String name;

    private BigDecimal pricePerNight;

    private Currency currency;

    private LocalDate validFrom;

    private LocalDate validTo;

    private String conditions;
}
