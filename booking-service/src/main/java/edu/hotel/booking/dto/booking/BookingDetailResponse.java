package edu.hotel.booking.dto.booking;

import edu.hotel.booking.dto.room.RoomSummaryResponse;
import edu.hotel.booking.dto.audit.BookingStatusHistoryResponse;
import edu.hotel.booking.model.BookingStatus;
import edu.hotel.booking.model.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class BookingDetailResponse {

    private Long id;

    private RoomSummaryResponse roomResponse;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private BookingStatus status;

    private BigDecimal totalPrice;

    private Currency currency;

    private String notes;

    private List<BookingStatusHistoryResponse> statusHistory;


}
