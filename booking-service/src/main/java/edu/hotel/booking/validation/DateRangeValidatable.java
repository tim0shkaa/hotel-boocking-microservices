package edu.hotel.booking.validation;

import java.time.LocalDate;

public interface DateRangeValidatable {

    LocalDate getDateFrom();

    LocalDate getDateTo();
}
