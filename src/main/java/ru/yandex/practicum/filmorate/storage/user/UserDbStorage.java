package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sqlQuery = "insert into USERS(login, name, birthday, email)"
                + "VALUES ( ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setDate(3, Date.valueOf(user.getBirthday()));
            stmt.setString(4, user.getEmail());
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update USERS set LOGIN = ?, NAME = ?, BIRTHDAY = ?, EMAIL = ?" +
                "where USER_ID = ?";
        int numRow = jdbcTemplate.update(sqlQuery, user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()), user.getEmail(),
                user.getId());
        if (numRow == 0) {
            log.warn("Ошибка валидации наличия юзера");
            throw new NotFoundException("Юзер с таким айди отсутствует");
        } else return user;
    }

    @Override
    public List<User> getUsers() {
        String sqlQuery = "select* from USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getUserById(int id) {
        try {
            String sqlQuery = "select* from USERS where USER_ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Ошибка валидации наличия юзера");
            throw new NotFoundException("Юзер с таким айди отсутствует");
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("USER_ID"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .email(resultSet.getString("EMAIL"))
                .build();
    }

    @Override
    public boolean addFriend(int userId, int friendId) {
        String sqlQuery = "insert into FRIENDSHIP(USER_ID, FRIEND_ID)"
                + "VALUES ( ?, ?)";
        return jdbcTemplate.update(sqlQuery, userId, friendId) == 1;
    }

    @Override
    public boolean removeFriend(int userId, int friendId) {
        String sqlQuery = "delete from FRIENDSHIP where USER_ID = ? and FRIEND_ID = ?";
        return jdbcTemplate.update(sqlQuery, userId, friendId) == 1;
    }

    @Override
    public List<User> getUserFriends(int userId) {
        String sqlQuery = "select USERS.USER_ID, LOGIN, NAME, BIRTHDAY, EMAIL" +
                " from FRIENDSHIP left join USERS on FRIENDSHIP.FRIEND_ID = USERS.USER_ID where FRIENDSHIP.USER_ID = ? ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int userId2) {
        String sqlQuery = "select USER_ID, LOGIN, NAME, BIRTHDAY, EMAIL from (select FRIEND_ID as FRIENDID from FRIENDSHIP where USER_ID = ? " +
                "intersect select FRIEND_ID from FRIENDSHIP where USER_ID = ?) left join USERS on FRIENDID=USER_ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, userId2);
    }
}
