package ru.yandex.practicum.filmorate.storage.directorStorage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {
    List<Director> findAll();

    Optional<Director> findById(Integer id);

    Director create(Director director);

    Director update(Director director);

    void delete(Integer id);

    boolean isDirectorExist(Integer id);

    Set<Director> getDirectors(int film_id);

    void updateFilmDirector(Film film);
}