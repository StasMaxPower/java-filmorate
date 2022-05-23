package ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public Collection<Film> getFilms();

    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public Film getFilmToId(int id);

    public Film checkId(int id);
}
