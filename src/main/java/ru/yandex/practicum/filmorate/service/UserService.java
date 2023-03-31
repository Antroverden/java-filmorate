package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
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

    public ArrayList<User> getUsers() {
        return new ArrayList<>(userStorage.getUsers().values());
    }

    public User addFriend(int idUser, int idFriend) {
        if (idFriend > 0) {
            getUserById(idUser).getFriends().add(idFriend);
            getUserById(idFriend).getFriends().add(idUser);
            return getUserById(idUser);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public User removeFriend(int id, int id2) {
        getUserById(id).getFriends().remove(id2);
        getUserById(id2).getFriends().remove(id);
        return getUserById(id);
    }

    private Set<Integer> showFriends(int id) {
        return getUserById(id).getFriends();
    }

    private ArrayList<User> getUsersByIds(Set<Integer> ids) {
        ArrayList<User> users = new ArrayList<>(ids.size());
        for (int i : ids) {
            users.add(getUserById(i));
        }
        return users;
    }

    public User getUserById(int id) {
        if (userStorage.getUsers().containsKey(id)) return userStorage.getUsers().get(id);
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public ArrayList<User> getUserFriends(int userId) {
        return getUsersByIds(showFriends(userId));
    }

    public ArrayList<User> getCommonFriends(int userId, int otherUserId) {
        Set<Integer> userOneFriends = new HashSet<>(showFriends(userId));
        userOneFriends.retainAll(showFriends(otherUserId));
        return getUsersByIds(userOneFriends);
    }
}
