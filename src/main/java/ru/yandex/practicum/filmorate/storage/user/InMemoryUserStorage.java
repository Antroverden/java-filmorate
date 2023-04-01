package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

@Repository
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public User addUser(User user) {
        user.setId(++id);
        users.put(id, user);
        log.info("Юзер добавлен");
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Ошибка валидации наличия юзера");
            throw new NotFoundException("Такого id нет");
        } else {
            users.put(user.getId(), user);
            log.info("Юзер обновлен");
            return user;
        }
    }

    @Override
    public HashMap<Integer, User> getUsers() {
        return users;
    }
}
