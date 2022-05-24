package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }


    @GetMapping(value = {"/films/popular"})
    public List<Film> getPopularFilms(@RequestParam (defaultValue = "10") int count){
        return filmService.getPopularFilms(count);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable int id,@PathVariable int userId){
        return filmService.deleteLike(id, userId);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLike(@PathVariable int id,@PathVariable int userId){
        return filmService.addLike(id, userId);
    }

    @GetMapping("/films/{id}")
     public Film getFilmToId(@PathVariable int id){
        return filmService.getFilmToId(id);
    }

    @GetMapping("/films")
    public Collection<Film> getFilms(){
        return filmService.getFilms();
    }

    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film){
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film){
        return filmService.updateFilm(film);
    }

}
