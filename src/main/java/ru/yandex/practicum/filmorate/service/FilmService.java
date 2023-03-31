package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Set;

@Service
public class FilmService {

    public static final String DATE_OF_FIRST_FILM_RELEASE = "28.12.1895";
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public ArrayList<Film> getFilms() {
        return new ArrayList<>(filmStorage.getFilms().values());
    }

    public Film addLike(int filmId, int userWhoLiked) {
        getFilmLikes(filmId).add(userWhoLiked);
        return getFilmById(filmId);
    }

    public Film removeLike(int filmId, int userWhoLiked) {
        if (getFilmLikes(filmId).contains(userWhoLiked)) {
            getFilmLikes(filmId).remove(userWhoLiked);
            return getFilmById(filmId);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    private Set<Integer> getFilmLikes(int filmId) {
        return getFilmById(filmId).getLikes();
    }

    public ArrayList<Film> getPopularFilms(int count) {
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
        if (filmStorage.getFilms().containsKey(id)) return filmStorage.getFilms().get(id);
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
