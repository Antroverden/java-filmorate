package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public Film addLike(int filmId, int userWhoLiked) {
        getFilmLikes(filmId).add(userWhoLiked);
        return getFilmById(filmId);
    }

    public Film removeLike(int filmId, int userWhoLiked) {
        if (getFilmLikes(filmId).contains(userWhoLiked)) {
            getFilmLikes(filmId).remove(userWhoLiked);
            return getFilmById(filmId);
        } else {
            log.warn("Ошибка валидации наличия пользователя");
            throw new NotFoundException("Пользователь отсутствует среди лайкнувших фильм");
        }
    }

    private Set<Integer> getFilmLikes(int filmId) {
        return getFilmById(filmId).getLikes();
    }

    public List<Film> getPopularFilms(int count) {
        ArrayList<Film> sortedFilms = new ArrayList<>(getFilms());
        sortedFilms.sort((f1, f2) -> f2.getLikes().size() - f1.getLikes().size());
        if (count == 0) {
            if (sortedFilms.size() <= 10) return sortedFilms;
            else return new ArrayList<>(sortedFilms.subList(0, 10));
        } else {
            if (sortedFilms.size() <= count) return sortedFilms;
            return new ArrayList<>(sortedFilms.subList(0, count));
        }
    }

    public Film getFilmById(int id) {
        if (id < 1) {
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
