package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@Builder
public class User {
    int id;
    @NotBlank
    @Email
    String email;
    @NotBlank
    String login;
    String name;
    @Past
    LocalDate birthday;
}