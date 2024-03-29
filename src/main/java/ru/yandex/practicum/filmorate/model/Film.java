package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.DateAfter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private static final String DATE_OF_FIRST_FILM_RELEASE = "28.12.1895";
    private int id;
    @NotBlank(message = "Имя не может состоять из пробелов")
    private String name;
    @Size(max = 200, message = "Описание должно быть не больше 200 символов")
    private String description;
    @DateAfter(date = DATE_OF_FIRST_FILM_RELEASE,
            message = "Дата релиза должна быть после 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность должна быть больше 0")
    private int duration;
    private final List<Genre> genres = new ArrayList<>();
    private Mpa mpa;
}

