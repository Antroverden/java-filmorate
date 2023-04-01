package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
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
        return new ArrayList<>(userStorage.getUsers().values());
    }

    public User addFriend(int idUser, int idFriend) {
        if (idFriend > 0) {
            getUserById(idUser).getFriends().add(idFriend);
            getUserById(idFriend).getFriends().add(idUser);
            return getUserById(idUser);
        } else {
            log.warn("Ошибка валидации наличия друга юзера");
            throw new NotFoundException("Друга юзера с таким айди нет");
        }
    }

    public User removeFriend(int id, int id2) {
        getUserById(id).getFriends().remove(id2);
        getUserById(id2).getFriends().remove(id);
        return getUserById(id);
    }

    private Set<Integer> getFriendsById(int id) {
        return getUserById(id).getFriends();
    }

    private List<User> getUsersByIds(Set<Integer> ids) {
        ArrayList<User> users = new ArrayList<>(ids.size());
        for (int i : ids) {
            users.add(getUserById(i));
        }
        return users;
    }

    public User getUserById(int id) {
        if (!userStorage.getUsers().containsKey(id)) {
            log.warn("Ошибка валидации наличия юзера");
            throw new NotFoundException("Юзера с таким айди нет");
        } else return userStorage.getUsers().get(id);
    }

    public List<User> getUserFriends(int userId) {
        return getUsersByIds(getFriendsById(userId));
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        Set<Integer> userOneFriends = new HashSet<>(getFriendsById(userId));
        userOneFriends.retainAll(getFriendsById(otherUserId));
        return getUsersByIds(userOneFriends);
    }
}
