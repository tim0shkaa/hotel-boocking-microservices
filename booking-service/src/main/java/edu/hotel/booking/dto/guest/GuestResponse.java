package edu.hotel.booking.dto.guest;

import lombok.Data;

@Data
public class GuestResponse {

    private Long id;

    private String firstName;

    private String surname;

    private String patronymic;

    private String phoneNumber;
}
