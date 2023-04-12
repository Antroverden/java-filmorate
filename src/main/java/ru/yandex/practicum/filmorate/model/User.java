package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.NoSpaces;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashMap;

@Data
@Builder
public class User {
    private final HashMap<Integer, Boolean> friends = new HashMap<>();
    private int id;
    @NotBlank(message = "Адрес электронной почты не может состоять из пробелов")
    @Email(message = "Адрес электронной почты составлен неправильно")
    private String email;
    @NotBlank(message = "Логин не может состоять из пробелов")
    @NoSpaces(message = "В логине не должно быть пробелов")
    private String login;
    private String name;
    @Past(message = "Дата дня рождения должна быть в прошлом")
    private LocalDate birthday;
}
