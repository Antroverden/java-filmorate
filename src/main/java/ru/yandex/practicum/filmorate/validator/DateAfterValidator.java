package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateAfterValidator implements ConstraintValidator<DateAfter, LocalDate> {

    private String date;

    @Override
    public void initialize(DateAfter constraintAnnotation) {
        this.date = constraintAnnotation.date();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);
        return value.isAfter(localDate);
    }
}
