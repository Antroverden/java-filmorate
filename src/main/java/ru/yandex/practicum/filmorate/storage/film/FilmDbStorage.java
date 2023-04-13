package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private static final String selectColumnsForFilm = "select FILM.FILM_ID, FILM.NAME, FILM.DESCRIPTION," +
            " FILM.RELEASE_DATE, FILM.DURATION, FILM.RATING_ID, RATING_NAME";

    private static final String queryForFilms = "select FILM.FILM_ID, FILM.NAME as FILMNAME, FILM.DESCRIPTION, FILM.RELEASE_DATE, FILM.DURATION, FILM.RATING_ID as RATINGID, RATING_NAME, genre.GENRE_ID as GENREID, genre.name from FILM left join RATING on FILM.RATING_ID = RATING.RATING_ID left join film_genre on film.film_id = FILM_GENRE.FIlM_ID left JOIN genre on film_genre.genre_id = genre.genre_id";
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "insert into FILM(name, description, release_date, duration, rating_id)"
                + "VALUES ( ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        addGenresToDB(film);
        return film;
    }

    private void addGenresToDB(Film film) {
        String sqlQueryForGenres = "merge into FILM_GENRE (FILM_ID, GENRE_ID) values (?, ?)";
        List<Genre> genres = film.getGenres();
        jdbcTemplate.batchUpdate(sqlQueryForGenres, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, film.getId());
                preparedStatement.setInt(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update FILM set NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ?" +
                "where FILM_ID = ?";
        int rowNum = jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        if (rowNum == 0) {
            log.warn("Ошибка валидации наличия фильма");
            throw new NotFoundException("Фильм с таким айди отсутствует");
        } else {
            String sqlDelete = "delete from FILM_GENRE where FILM_ID = ?";
            jdbcTemplate.update(sqlDelete, film.getId());
            addGenresToDB(film);
            film.getGenres().clear();
            HashMap<Integer, Film> filmForGenre = new HashMap<>(Map.of(film.getId(), film));
            String queryToAddGenre = "select FILM_ID, FILM_GENRE.GENRE_ID as GENREID, GENRE.NAME" +
                    " from FILM_GENRE left join GENRE on FILM_GENRE.GENRE_ID = GENRE.GENRE_ID";
            List<Map<String, Object>> filmsToGenres = jdbcTemplate.queryForList(queryToAddGenre);
            filmsToGenres.forEach(rowMap -> {
                int id = (int) rowMap.get("FILM_ID");
                int genreId = (int) rowMap.get("GENREID");
                String genreName = (String) rowMap.get("NAME");
                if (filmForGenre.containsKey(id)) {
                    filmForGenre.get(id).getGenres().add(new Genre(genreId, genreName));
                }
            });
            return filmForGenre.get(film.getId());
        }
    }

    @Override
    public List<Film> getFilms() {
        List<Map<String, Object>> filmsRows = jdbcTemplate.queryForList(queryForFilms);
        return new ArrayList<>(constructFilmFromDB(filmsRows).values());
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = queryForFilms + " where FILM.FILM_ID = ?";
        List<Map<String, Object>> filmRow = jdbcTemplate.queryForList(sqlQuery, id);
        Map<Integer, Film> film = constructFilmFromDB(filmRow);
        if (film.isEmpty()) {
            log.warn("Ошибка валидации наличия фильма");
            throw new NotFoundException("Фильм с таким айди отсутствует");
        } else return film.get(id);
    }

    private Map<Integer, Film> constructFilmFromDB(List<Map<String, Object>> filmsRows) {
        Map<Integer, Film> films = new HashMap<>();
        filmsRows.forEach(rowMap -> {
            int id = (int) rowMap.get("FILM_ID");
            String name = (String) rowMap.get("FILMNAME");
            String description = (String) rowMap.get("DESCRIPTION");
            LocalDate date = ((Date) rowMap.get("RELEASE_DATE")).toLocalDate();
            int duration = (int) rowMap.get("DURATION");
            int ratingId = (int) rowMap.get("RATINGID");
            String ratingName = (String) rowMap.get("RATING_NAME");
            Integer genreId = (Integer) rowMap.get("GENREID");
            String genreName = (String) rowMap.get("NAME");
            films.computeIfAbsent(id, f -> new Film(id, name, description, date, duration, new Mpa(ratingId, ratingName)));
            if (genreId != null) {
                films.get(id).getGenres().add(new Genre(genreId, genreName));
            }
        });
        return films;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getInt("DURATION"))
                .mpa(new Mpa(resultSet.getInt("RATING_ID"), resultSet.getString("RATING_NAME")))
                .build();
    }

    @Override
    public boolean addLike(int filmId, int userId) {
        String sqlQuery = "insert into LIKES(FILM_ID, USER_ID)"
                + "VALUES ( ?, ?)";
        return jdbcTemplate.update(sqlQuery, filmId, userId) == 1;
    }

    @Override
    public boolean removeLike(int filmId, int userId) {
        String sqlQuery = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        return jdbcTemplate.update(sqlQuery, filmId, userId) == 1;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "select FILM.FILM_ID, FILM.NAME, FILM.DESCRIPTION, FILM.RELEASE_DATE, FILM.DURATION," +
                "FILM.RATING_ID, RATING_NAME from (select FILM_ID, COUNT(USER_ID) as count" +
                " from LIKES group by FILM_ID) as s right join FILM on s.FILM_ID = FILM.FILM_ID" +
                " left join RATING on FILM.RATING_ID = RATING.RATING_ID order by count desc limit ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }
}
