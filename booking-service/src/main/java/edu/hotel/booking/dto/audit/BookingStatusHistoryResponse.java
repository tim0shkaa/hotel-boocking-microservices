package edu.hotel.booking.dto.audit;

import edu.hotel.booking.model.BookingStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingStatusHistoryResponse {

    private BookingStatus status;

    private LocalDateTime changedAt;

    private String changedBy;

    private String reason;
}
