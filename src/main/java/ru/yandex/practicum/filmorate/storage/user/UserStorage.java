package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;

public interface UserStorage {
    User addUser(@Valid @RequestBody User user);

    User updateUser(@Valid @RequestBody User user);

    HashMap<Integer, User> getUsers();
}
