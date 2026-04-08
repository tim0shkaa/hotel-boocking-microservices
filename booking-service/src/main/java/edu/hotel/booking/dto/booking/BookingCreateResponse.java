package edu.hotel.booking.dto.booking;

import lombok.Data;

@Data
public class BookingCreateResponse {

    private Long bookingId;

    private String paymentUrl;
}
