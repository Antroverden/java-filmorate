package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return new ArrayList<>(filmStorage.getFilms());
    }

    public HttpStatus addLike(int filmId, int userWhoLiked) {
        if ((filmId > 0) && (userWhoLiked > 0) && (filmStorage.addLike(filmId, userWhoLiked))) {
            return HttpStatus.OK;
        } else {
            log.warn("Ошибка валидации наличия юзера или фильма");
            throw new NotFoundException("Юзера или фильма с такими айди нет");
        }
    }

    public HttpStatus removeLike(int filmId, int userWhoLiked) {
        if (filmStorage.removeLike(filmId, userWhoLiked)) {
            return HttpStatus.OK;
        } else {
            log.warn("Ошибка валидации наличия юзера или друга юзера");
            throw new NotFoundException("Такого Юзера или друга нет");
        }
    }


    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public Film getFilmById(int id) {
        if (id < 0) {
            log.warn("Ошибка валидации наличия фильма");
            throw new NotFoundException("Фильм с таким айди отсутствует");
        } else return filmStorage.getFilmById(id);
    }

    public Mpa getMpaById(int id) {
        return filmStorage.getMpaById(id);
    }

    public List<Mpa> getMpas() {
        return filmStorage.getMpas();
    }

    public Genre getGenreById(int id) {
        return filmStorage.getGenreById(id);
    }

    public List<Genre> getGenres() {
        return filmStorage.getGenres();
    }
}
