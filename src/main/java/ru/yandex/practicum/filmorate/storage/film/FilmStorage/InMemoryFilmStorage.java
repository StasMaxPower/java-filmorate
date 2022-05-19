package ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @Override
    public Collection<Film> getFilms(){
        return films.values();
    }

    @Override
    public Film addFilm(Film film){
        checkFilm(film);
        if (films.containsKey(film.getId())){
            log.info("Такой фильм уже существует");
            throw new ValidationException("Такой фильм уже существует");
        }
        films.put(film.getId(),film);
        return film;
    }

    @Override
    public Film updateFilm(Film film){
        checkFilm(film);
        films.put(film.getId(),film);
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
