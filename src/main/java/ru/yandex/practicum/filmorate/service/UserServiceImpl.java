package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(userStorage.getUsers());
    }

    @Override
    public HttpStatus addFriend(int idUser, int idFriend) {
        if ((idFriend > 0) && (userStorage.addFriend(idUser, idFriend))) {
            return HttpStatus.OK;
        } else {
            log.warn("Ошибка валидации наличия друга юзера");
            throw new NotFoundException("Друга юзера с таким айди нет");
        }
    }

    @Override
    public HttpStatus removeFriend(int id, int id2) {
        if (userStorage.removeFriend(id, id2)) {
            return HttpStatus.OK;
        } else {
            log.warn("Ошибка валидации наличия юзера или друга юзера");
            throw new NotFoundException("Такого Юзера или друга нет");
        }
    }

    @Override
    public User getUserById(int id) {
        if (id < 1) {
            log.warn("Ошибка валидации наличия юзера");
            throw new NotFoundException("Юзера с таким айди нет");
        } else return userStorage.getUserById(id);
    }

    @Override
    public List<User> getUserFriends(int userId) {
        return userStorage.getUserFriends(userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
    }
}
