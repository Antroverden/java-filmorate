package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return new ArrayList<>(userStorage.getUsers());
    }

    public HttpStatus addFriend(int idUser, int idFriend) {
        if ((idFriend > 0) && (userStorage.addFriend(idUser, idFriend))) {
            return HttpStatus.OK;
        } else {
            log.warn("Ошибка валидации наличия друга юзера");
            throw new NotFoundException("Друга юзера с таким айди нет");
        }
    }

    public HttpStatus removeFriend(int id, int id2) {
        if (userStorage.removeFriend(id, id2)) {
            return HttpStatus.OK;
        } else {
            log.warn("Ошибка валидации наличия юзера или друга юзера");
            throw new NotFoundException("Такого Юзера или друга нет");
        }
    }

    private Set<Integer> getFriendsById(int id) {
        return getUserById(id).getFriends().keySet();
    }

    private List<User> getUsersByIds(Set<Integer> ids) {
        ArrayList<User> users = new ArrayList<>(ids.size());
        for (int i : ids) {
            users.add(getUserById(i));
        }
        return users;
    }

    public User getUserById(int id) {
        if (id < 1) {
            log.warn("Ошибка валидации наличия юзера");
            throw new NotFoundException("Юзера с таким айди нет");
        } else return userStorage.getUserById(id);
    }

    public List<User> getUserFriends(int userId) {
        return userStorage.getUserFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
    }
}
