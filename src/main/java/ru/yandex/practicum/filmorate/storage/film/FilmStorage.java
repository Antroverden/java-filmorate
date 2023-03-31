package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashMap;

public interface FilmStorage {
    @PostMapping
    Film addFilm(@Valid @RequestBody Film film);

    @PutMapping
    Film updateFilm(@Valid @RequestBody Film film);

    @GetMapping
    HashMap<Integer,Film> getFilms();
}
