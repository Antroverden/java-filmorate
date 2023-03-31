package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.util.HashMap;

@Component
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
            throw new ValidationException("Такого id нет");
        }
        users.put(user.getId(), user);
        log.info("Юзер обновлен");
        return user;
    }

    @Override
    public HashMap<Integer,User> getUsers() {
        return users;
    }
}
