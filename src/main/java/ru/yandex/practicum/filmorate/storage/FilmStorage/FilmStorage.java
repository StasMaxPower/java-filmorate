package ru.yandex.practicum.filmorate.storage.FilmStorage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {


    public Film addLike( int filmId,  int userId);
    public List<Film> getPopularFilms(int count);
    public Film deleteLike(int id, int filmId);

    public Collection<Film> getFilms();

    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public Film getFilmToId(int id);

    public Film checkFilmId(int id);
}
