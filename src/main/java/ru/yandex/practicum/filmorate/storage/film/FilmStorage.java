package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

public interface FilmStorage {

    Film addFilm(@Valid @RequestBody Film film);

    Film updateFilm(@Valid @RequestBody Film film);

    List<Film> getFilms();

    Film getFilmById(int id);

    boolean addLike(int userId, int friendId);

    boolean removeLike(int userId, int friendId);

    List<Film> getPopularFilms(int count);
}
