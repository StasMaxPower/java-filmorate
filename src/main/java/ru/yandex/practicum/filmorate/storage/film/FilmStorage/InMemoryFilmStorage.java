package ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        if (films.containsKey(film.getId())){
            log.info("Такой фильм уже существует");
            throw new ValidationException("Такой фильм уже существует");
        }
        films.put(film.getId(),film);
        return film;
    }

    @Override
    public Film updateFilm(Film film){
        checkId(film.getId());
        films.put(film.getId(),film);
        return film;
    }

    @Override
    public Film getFilmToId(int id){
        return films.values().stream()
                .filter(x->x.getId() == id)
                .findFirst().orElseGet(()->checkId(id));
    }

    @Override
    public Film checkId(int id){
        if (!films.containsKey(id)){
            log.info("Фильм не найден");
            throw new NotFoundException("Фильм не найден");
        }
        return films.get(id);
    }


}
