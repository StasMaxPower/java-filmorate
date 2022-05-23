package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private int filmId;


    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        filmId = 0;
    }

    public List<Film> getPopularFilms(int count){
        log.info("Запрос на вывод популярных фильмов получен.");
        return getFilms().stream()
                .sorted((p1,p2) -> p2.getLikes().size()-p1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film deleteLike(int id, int userId){
        filmStorage.checkId(userId);
        log.info("Запрос на удаление лайка получен.");
        getFilmToId(id).deleteLike(userId);
        return getFilmToId(id);
    }

    public Film addLike(int id, int userId){
        log.info("Запрос на добавление лайка к фильму получен.");
        getFilmToId(id).addLike(userId);
        return getFilmToId(id);
    }

    public Film getFilmToId(int id){
        log.info("Запрос на вывод фильма по ID получен.");
        return filmStorage.getFilmToId(id);
    }

    public Collection<Film> getFilms(){
        log.info("Запрос на вывод фильмов получен.");
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film){
        log.info("Запрос на добавление фильма получен.");
        checkFilm(film);
        film.setId(++filmId);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film){
        log.info("Запрос на обновление фильма получен.");
        checkFilm(film);
        return filmStorage.updateFilm(film);
    }

    public void checkFilm(Film film){
        if (film.getName().isEmpty()||film.getName().isBlank()) {
            log.info("Название фильма не задано");
            throw new ru.yandex.practicum.filmorate.exception.ValidationException("Название фильма не задано");
        }
        if (film.getDescription().length()>200||film.getDescription().isBlank()) {
            log.info("Описание фильма не более 200 символов.");
            throw new ru.yandex.practicum.filmorate.exception.ValidationException("Описание фильма не более 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            log.info("Дата выхода фильма не может быть до 29.12.1895");
            throw new ru.yandex.practicum.filmorate.exception.ValidationException("Дата выхода фильма не может быть до 29.12.1895");
        }
        if (film.getDuration()<0) {
            log.info("Продолжительность фильма не может быть отрицательной");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
    }
}
