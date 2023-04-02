package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SpacesValidator.class)
public @interface NoSpaces {
    String message() default "В логине не могут быть пробелы";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
