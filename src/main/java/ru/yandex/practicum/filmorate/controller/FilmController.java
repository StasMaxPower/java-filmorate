package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping("/films")
    public Collection<Film> getFilms(){
        log.info("Запрос на вывод фильмов получен.");
        return films.values();
    }

    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film){
        checkFilm(film);
        if (films.containsKey(film.getId())){
            log.info("Такой фильм уже существует");
            throw new ValidationException("Такой фильм уже существует");
        }
        films.put(film.getId(),film);
        log.info("Запрос на добавление фильма получен.");
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film){
        checkFilm(film);
        films.put(film.getId(),film);
        log.info("Запрос на обновление фильма получен.");
        return film;
    }

    public void checkFilm(Film film){
        if (film.getName().isEmpty()||film.getName().isBlank()) {
            log.info("Название фильма не задано");
            throw new ValidationException("Название фильма не задано");
        }
        if (film.getDescription().length()>200||film.getDescription().isBlank()) {
            log.info("Описание фильма не более 200 символов.");
            throw new ValidationException("Описание фильма не более 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            log.info("Дата выхода фильма не может быть до 29.12.1895");
            throw new ValidationException("Дата выхода фильма не может быть до 29.12.1895");
        }
        if (film.getDuration()<0) {
            log.info("Продолжительность фильма не может быть отрицательной");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
    }

}
