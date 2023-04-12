package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.validation.Valid;
import java.util.List;

public interface FilmStorage {

    Film addFilm(@Valid @RequestBody Film film);

    Film updateFilm(@Valid @RequestBody Film film);

    List<Film> getFilms();

    Film getFilmById(int id);

    Mpa getMpaById(int id);

    List<Mpa> getMpas();

    Genre getGenreById(int id);

    List<Genre> getGenres();
}
