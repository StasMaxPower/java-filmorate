package ru.yandex.practicum.filmorate.storage.filmStorage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {


    //public Film addLike( int filmId,  int userId);
    public List<Film> getPopular(int count);
    //public Film deleteLike(int id, int filmId);

    public List<Film> getBySearch(String query, List<String> by);

    public Collection<Film> getAll();

    public Film add(Film film);

    public Film update(Film film);

    public Film getToId(int id);

    public Film checkFilmId(int id);
}
