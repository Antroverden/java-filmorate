package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.NoSpaces;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private final Set<Integer> friends = new HashSet<>();
    private int id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @NoSpaces
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
