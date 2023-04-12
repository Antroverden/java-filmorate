package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserStorage {
    User addUser(@Valid @RequestBody User user);

    User updateUser(@Valid @RequestBody User user);

    List<User> getUsers();

    User getUserById(int id);

    boolean addFriend(int userId, int friendId);

    boolean removeFriend(int userId, int friendId);

    List<User> getUserFriends(int userId);

    List<User> getCommonFriends(int userId, int userId2);
}
