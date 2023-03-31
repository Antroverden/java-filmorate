package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.DateAfter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private final Set<Integer> likes = new HashSet<>();
    private int id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @DateAfter(date = FilmService.DATE_OF_FIRST_FILM_RELEASE,
            message = "Дата релиза должна быть после 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Positive
    private int duration;
}
