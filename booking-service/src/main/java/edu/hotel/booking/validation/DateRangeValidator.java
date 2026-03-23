package edu.hotel.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, DateRangeValidatable> {

    @Override
    public boolean isValid(DateRangeValidatable dateRangeValidatable, ConstraintValidatorContext constraintValidatorContext) {
        if (dateRangeValidatable.getDateTo() == null || dateRangeValidatable.getDateFrom() == null) {
            return true;
        }
        return dateRangeValidatable.getDateFrom().isBefore(dateRangeValidatable.getDateTo());
    }
}
