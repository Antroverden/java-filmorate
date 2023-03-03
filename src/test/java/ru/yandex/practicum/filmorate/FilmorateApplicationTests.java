package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FilmController controller;
    @Autowired
    private UserController userController;
    @Autowired
    private ObjectMapper objectMapper;

    Film validFilm = Film.builder().name("Example").description("Description").duration(90)
            .releaseDate(LocalDate.of(2022, 12, 12)).build();
    Film invalidFilm = Film.builder().name(null).description("Description").duration(90)
            .releaseDate(LocalDate.of(999, 12, 12)).build();

    User validUser = User.builder().name("Username").email("example@gmail.com").login("Login")
            .birthday(LocalDate.of(1998, 12, 12)).build();

    User invalidUser = User.builder().name("Username").email("example-gmail.com").login(null)
            .birthday(LocalDate.of(2222, 12, 12)).build();

    @Test
    public void getUsers() throws Exception {
        userController.addUser(validUser);
        mockMvc.perform(get("/users")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString(objectMapper.writeValueAsString(validUser))));
    }

    @Test
    public void addUser() throws Exception {
        mockMvc.perform(post("/users").contentType("application/json")
                        .content(objectMapper.writeValueAsString(validUser))).andDo(print()).andExpect(status().isOk())
                .andExpect(content()
                        .string(containsString(objectMapper.writeValueAsString(userController.getUsers().get(0)))));
    }

    @Test
    public void addInvalidUser() throws Exception {
        mockMvc.perform(post("/users").contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUser))).andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updateUser() throws Exception {
        userController.addUser(validUser);
        User validUser1 = User.builder().name("User").email("exampl@gmail.com").login("Loginqq")
                .birthday(LocalDate.of(1999, 11, 12)).id(1).build();
        mockMvc.perform(put("/users").contentType("application/json")
                        .content(objectMapper.writeValueAsString(validUser1))).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString(objectMapper.writeValueAsString(validUser1))));
    }

    @Test
    public void updateInvalidUser() throws Exception {
        invalidUser.setId(1);
        mockMvc.perform(put("/users").contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUser))).andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getFilms() throws Exception {
        controller.addFilm(validFilm);
        mockMvc.perform(get("/films")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString(objectMapper.writeValueAsString(validFilm))));
    }

    @Test
    public void addFilm() throws Exception {
        mockMvc.perform(post("/films").contentType("application/json")
                        .content(objectMapper.writeValueAsString(validFilm))).andDo(print()).andExpect(status().isOk())
                .andExpect(content()
                        .string(containsString(objectMapper.writeValueAsString(controller.getFilms().get(0)))));
    }

    @Test
    public void addInvalidFilm() throws Exception {
        mockMvc.perform(post("/films").contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidFilm))).andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updateFilm() throws Exception {
        controller.addFilm(validFilm);
        Film validFilm1 = Film.builder().name("Exam").description("Descr").duration(88)
                .releaseDate(LocalDate.of(2022, 12, 13)).id(1).build();
        mockMvc.perform(put("/films").contentType("application/json")
                        .content(objectMapper.writeValueAsString(validFilm1))).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString(objectMapper.writeValueAsString(validFilm1))));
    }

    @Test
    public void updateInvalidFilm() throws Exception {
        invalidFilm.setId(1);
        mockMvc.perform(put("/films").contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidFilm))).andDo(print())
                .andExpect(status().is4xxClientError());
    }

}
