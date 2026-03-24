package edu.hotel.booking.dto.booking;

import edu.hotel.booking.model.BookingStatus;
import edu.hotel.booking.model.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingSummaryResponse {

    private Long id;

    private Long roomTypeId;

    private String roomTypeName;

    private String hotelName;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private BookingStatus status;

    private BigDecimal totalPrice;

    private Currency currency;
}
