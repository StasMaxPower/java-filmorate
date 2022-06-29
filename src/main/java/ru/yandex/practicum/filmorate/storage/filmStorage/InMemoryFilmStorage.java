package ru.yandex.practicum.filmorate.storage.filmStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();


    //@Override
    public Film addLike(int filmId, int userId) {
        return null;
    }

    @Override
    public List<Film> getPopular(int count) {
        return null;
    }

    //@Override
    public Film deleteLike(int id, int filmId){
        checkFilmId(filmId);
       return getToId(id).deleteLike(filmId);
    }
    @Override
    public Collection<Film> getAll(){
        return films.values();
    }

    @Override
    public Film add(Film film){
        if (films.containsKey(film.getId())){
            log.info("Такой фильм уже существует");
            throw new ValidationException("Такой фильм уже существует");
        }
        films.put(film.getId(),film);
        return film;
    }

    @Override
    public Film update(Film film){
        checkFilmId(film.getId());
        films.put(film.getId(),film);
        return film;
    }

    @Override
    public Film getToId(int id){
        return films.values().stream()
                .filter(x->x.getId() == id)
                .findFirst().orElseGet(()->checkFilmId(id));
    }

    @Override
    public void deleteFilm(int id){
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Film checkFilmId(int id){
        if (!films.containsKey(id)){
            log.info("Фильм не найден");
            throw new NotFoundException("Фильм не найден");
        }
        return films.get(id);
    }

}
