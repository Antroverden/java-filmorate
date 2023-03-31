package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;

public interface UserStorage {
    @PostMapping
    User addUser(@Valid @RequestBody User user);

    @PutMapping
    User updateUser(@Valid @RequestBody User user);

    @GetMapping
    HashMap<Integer,User> getUsers();
}
