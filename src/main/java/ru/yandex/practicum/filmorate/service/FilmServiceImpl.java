package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.MpaDbStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    public FilmServiceImpl(FilmStorage filmStorage, GenreDbStorage genreDbStorage, MpaDbStorage mpaDbStorage) {
        this.filmStorage = filmStorage;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    @Override
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(filmStorage.getFilms());
    }

    @Override
    public HttpStatus addLike(int filmId, int userWhoLiked) {
        if ((filmId > 0) && (userWhoLiked > 0) && (filmStorage.addLike(filmId, userWhoLiked))) {
            return HttpStatus.OK;
        } else {
            log.warn("Ошибка валидации наличия юзера или фильма");
            throw new NotFoundException("Юзера или фильма с такими айди нет");
        }
    }

    @Override
    public HttpStatus removeLike(int filmId, int userWhoLiked) {
        if (filmStorage.removeLike(filmId, userWhoLiked)) {
            return HttpStatus.OK;
        } else {
            log.warn("Ошибка валидации наличия юзера или друга юзера");
            throw new NotFoundException("Такого Юзера или друга нет");
        }
    }


    @Override
    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    @Override
    public Film getFilmById(int id) {
        if (id < 0) {
            log.warn("Ошибка валидации наличия фильма");
            throw new NotFoundException("Фильм с таким айди отсутствует");
        } else return filmStorage.getFilmById(id);
    }

    @Override
    public Mpa getMpaById(int id) {
        return mpaDbStorage.getMpaById(id);
    }

    @Override
    public List<Mpa> getMpas() {
        return mpaDbStorage.getMpas();
    }

    @Override
    public Genre getGenreById(int id) {
        return genreDbStorage.getGenreById(id);
    }

    @Override
    public List<Genre> getGenres() {
        return genreDbStorage.getGenres();
    }
}
