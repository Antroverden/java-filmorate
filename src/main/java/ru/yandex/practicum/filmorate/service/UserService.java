package ru.yandex.practicum.filmorate.service;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    HttpStatus addFriend(int idUser, int idFriend);

    HttpStatus removeFriend(int id, int id2);

    User getUserById(int id);

    List<User> getUserFriends(int userId);

    List<User> getCommonFriends(int userId, int otherUserId);
}
