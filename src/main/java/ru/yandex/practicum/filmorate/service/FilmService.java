package ru.yandex.practicum.filmorate.service;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmService {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    HttpStatus addLike(int filmId, int userWhoLiked);

    HttpStatus removeLike(int filmId, int userWhoLiked);

    List<Film> getPopularFilms(int count);

    Film getFilmById(int id);

    Mpa getMpaById(int id);

    List<Mpa> getMpas();

    Genre getGenreById(int id);

    List<Genre> getGenres();
}
