package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    Film film = new Film(1,"Matrix", "description", LocalDate.of(2000,01,01), 100);
    Film filmWithoutName = new Film(1,"", "description", LocalDate.of(2000,01,01), 50);
    Film filmWitInvalidDesc = new Film(1,"Matrix", "description".repeat(20), LocalDate.of(2000,01,01), 20);
    Film filmWitInvalidDate = new Film(1,"Matrix", "description", LocalDate.of(1895,01,01), 30);
    Film filmWitInvalidDuration = new Film(1,"Matrix", "description", LocalDate.of(2000,01,01), -1);

    @Test
    void createValidFilmResponseShouldBeOk() throws Exception {
        this.mockMvc.perform(post("/films")
                        .content(mapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    void createFilmWithInvalidDescription() throws Exception {
        this.mockMvc.perform(post("/films")
                        .content(mapper.writeValueAsString(filmWitInvalidDesc))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmWithInvalidDate() throws Exception {
        this.mockMvc.perform(post("/films")
                        .content(mapper.writeValueAsString(filmWithoutName))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmWithInvalidName() throws Exception {
        this.mockMvc.perform(post("/films")
                        .content(mapper.writeValueAsString(filmWitInvalidDate))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmWithInvalidDuration() throws Exception {
            this.mockMvc.perform(post("/films")
                    .content(mapper.writeValueAsString(filmWitInvalidDuration))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
    }
}