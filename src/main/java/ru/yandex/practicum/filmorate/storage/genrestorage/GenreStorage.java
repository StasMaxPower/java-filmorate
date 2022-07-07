package ru.yandex.practicum.filmorate.storage.genrestorage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    public List<Genre> getAllGenre();

    public Genre getGenreToId(int id);

    public List<Genre> getGenres(int id);

    public void updateFilmGenre(Film film);

    public void checkId(int id);

}
