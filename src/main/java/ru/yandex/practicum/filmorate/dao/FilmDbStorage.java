package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

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
        String sqlQueryForGenres = "insert into FILM_GENRE(FILM_ID, GENRE_ID)"
                + "VALUES ( ?, ?)";
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
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update FILM set NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ?" +
                "where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        return film;
    }

//    @Override
//    public List<Film> getFilms() {
//        String sqlQuery = "select* from FILM left join RATING on FILM.RATING_ID = RATING.RATING_ID";
//        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
//        sqlQuery = "select FILM_ID, FILM_GENRE.GENRE_ID as GENREID, GENRE.NAME" +
//                " from FILM_GENRE left join GENRE on FILM_GENRE.GENRE_ID = GENRE.GENRE_ID";
//        List<Map<String, Object>> filmsToGenres = jdbcTemplate.queryForList(sqlQuery);
//        filmsToGenres.forEach(rowMap -> {
//            int id = (int) rowMap.get("ID");
//            int genreId = (int) rowMap.get("GENREID");
//            String genrename = (String) rowMap.get("NAME");
//            films.get(id).getGenre().add(new Genre(genreId, genrename));
//        });
//        return films;
//    }


    private void addGenresToFilms(Map<Integer, Film> films) {
        String queryToAddGenre = "select FILM_ID, FILM_GENRE.GENRE_ID as GENREID, GENRE.NAME" +
                " from FILM_GENRE left join GENRE on FILM_GENRE.GENRE_ID = GENRE.GENRE_ID";
        List<Map<String, Object>> filmsToGenres = jdbcTemplate.queryForList(queryToAddGenre);
        filmsToGenres.forEach(rowMap -> {
            int id = (int) rowMap.get("ID");
            int genreId = (int) rowMap.get("GENREID");
            String genrename = (String) rowMap.get("NAME");
            films.get(id).getGenres().add(new Genre(genreId, genrename));
        });
    }

    @Override
    public List<Film> getFilms() {
        String queryForFilm = "select FILM.FILM_ID, FILM.NAME as FILMNAME, FILM.DESCRIPTION, FILM.RELEASE_DATE," +
                " FILM.DURATION, FILM.RATING_ID as RATINGID, RATING.NAME" +
                " from FILM left join RATING on FILM.RATING_ID = RATING.RATING_ID";
        Map<Integer, Film> films = new HashMap<>();
        List<Map<String, Object>> filmsRating = jdbcTemplate.queryForList(queryForFilm);
        filmsRating.forEach(rowMap -> {
            int id = (int) rowMap.get("FILM_ID");
            String name = (String) rowMap.get("FILMNAME");
            String description = (String) rowMap.get("DESCRIPTION");
            LocalDate date = ((Date) rowMap.get("RELEASE_DATE")).toLocalDate();
            int duration = (int) rowMap.get("DURATION");
            int ratingId = (int) rowMap.get("RATINGID");
            String ratingName = (String) rowMap.get("NAME");
            films.put(id, new Film(id, name, description, date, duration, new Mpa(ratingId, ratingName)));
        });
        addGenresToFilms(films);
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = "select* from FILM left join RATING on FILM.RATING_ID = RATING.RATING_ID where FILM_ID = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery, BeanPropertyRowMapper.newInstance(Film.class), id);
        Map<Integer, Film> films = new HashMap<>();
        int filmId = film.getId();
        films.put(filmId, film);
        addGenresToFilms(films);
        return films.get(filmId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getInt("DURATION"))
                .mpa(new Mpa(resultSet.getInt("RATING_ID"), resultSet.getString("NAME")))
                .build();
    }

    @Override
    public Mpa getMpaById(int id) {
        String sqlQuery = "select* from RATING where RATING_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, BeanPropertyRowMapper.newInstance(Mpa.class), id);
    }

    @Override
    public List<Mpa> getMpas() {
        String sqlQuery = "select* from RATING";
        return jdbcTemplate.query(sqlQuery, BeanPropertyRowMapper.newInstance(Mpa.class));
    }

//    private List<Genre> getGenresByFilmId(int id) {
//        String sqlQuery = "select* from FILM_GENRE where FILM_ID = ?";
//        return jdbcTemplate.query(sqlQuery, BeanPropertyRowMapper.newInstance(Genre.class), id);
//    }

    @Override
    public Genre getGenreById(int id) {
        String sqlQuery = "select* from GENRE where GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, BeanPropertyRowMapper.newInstance(Genre.class), id);
    }

    @Override
    public List<Genre> getGenres() {
        String sqlQuery = "select* from GENRE";
        return jdbcTemplate.query(sqlQuery, BeanPropertyRowMapper.newInstance(Genre.class));
    }
}
