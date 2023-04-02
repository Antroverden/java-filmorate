package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

@Repository
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @Override
    public Film addFilm(Film film) {
        film.setId(++id);
        films.put(id, film);
        log.info("Фильм добавлен");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Ошибка валидации наличия фильма");
            throw new NotFoundException("Такого id нет");
        } else {
            films.put(film.getId(), film);
            log.info("Фильм обновлен");
            return film;
        }
    }

    @Override
    public HashMap<Integer, Film> getFilms() {
        return films;
    }
}
