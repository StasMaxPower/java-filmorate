package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.FilmStorage;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private FilmStorage filmStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);


    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }


    public List<Film> getPopularFilms(int count){
        log.info("Запрос на вывод популярных фильмов получен.");
        return getFilms().stream()
                .sorted((p1,p2) -> p2.getLikes().size()-p1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film deleteLike(int id, int userId){
        getFilmToId(userId);
        log.info("Запрос на удаление лайка получен.");
        getFilmToId(id).getLikes().remove(userId);
        return getFilmToId(id);
    }

    public Film addLike(int id, int userId){
        log.info("Запрос на добавление лайка к фильму получен.");
        getFilmToId(id).getLikes().add(userId);
        return getFilmToId(id);
    }

    public Film getFilmToId(int id){
        log.info("Запрос на вывод фильма по ID получен.");
        return filmStorage.getFilms().stream()
                .filter(x->x.getId()==id)
                .findFirst()
                .orElseThrow(()->new NotFoundException("Фильм не найден"));
    }

    public Collection<Film> getFilms(){
        log.info("Запрос на вывод фильмов получен.");
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film){
        log.info("Запрос на добавление фильма получен.");
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film){
        log.info("Запрос на обновление фильма получен.");
        return filmStorage.updateFilm(film);
    }

}
