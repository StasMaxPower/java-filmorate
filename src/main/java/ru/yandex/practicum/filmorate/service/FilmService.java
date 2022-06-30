package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likesStorage.LikesStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private int filmId;


    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, LikesStorage likesStorage) {
        this.filmStorage = filmStorage;
        this.likesStorage = likesStorage;
        filmId = 0;
    }

    public List<Film> getPopularFilms(int count, int genreId, int year){
        log.info("Запрос на вывод популярных фильмов получен.");
        return filmStorage.getPopular(count, genreId, year);
    }

    public Film deleteLike(int id, int filmId) {
        return likesStorage.deleteLike(id, filmId);
    }

    public Film addLike(int id, int filmId) {
        log.info("Запрос на добавление лайка к фильму получен.");
        return likesStorage.addLike(id, filmId);
    }

    public Film getFilmToId(int id) {
        log.info("Запрос на вывод фильма по ID получен.");
        return filmStorage.getToId(id);
    }

    public Collection<Film> getFilms() {
        log.info("Запрос на вывод фильмов получен.");
        return filmStorage.getAll();
    }

    public Film addFilm(Film film) {
        log.info("Запрос на добавление фильма получен.");
        checkFilm(film);
        //film.setId(++filmId);
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        log.info("Запрос на обновление фильма получен.");
        checkFilm(film);
        Film updateFilm = filmStorage.update(film);
        if (film.getGenres() == null)
            updateFilm.setGenres(null);
        else if (film.getGenres().isEmpty())
            updateFilm.setGenres(new ArrayList<Genre>());
        return updateFilm;
    }

    public void deleteFilm(int id) {
        log.info("Delete request received");
        filmStorage.deleteFilm(id);
    }

    public void checkFilm(Film film) {
        if (film.getName().isEmpty() || film.getName().isBlank()) {
            log.info("Название фильма не задано");
            throw new ru.yandex.practicum.filmorate.exception.ValidationException("Название фильма не задано");
        }
        if (film.getDescription().length() > 200 || film.getDescription().isBlank()) {
            log.info("Описание фильма не более 200 символов.");
            throw new ru.yandex.practicum.filmorate.exception.ValidationException("Описание фильма не более 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата выхода фильма не может быть до 29.12.1895");
            throw new ru.yandex.practicum.filmorate.exception.ValidationException("Дата выхода фильма не может быть до 29.12.1895");
        }
        if (film.getDuration() < 0) {
            log.info("Продолжительность фильма не может быть отрицательной");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
    }

}
