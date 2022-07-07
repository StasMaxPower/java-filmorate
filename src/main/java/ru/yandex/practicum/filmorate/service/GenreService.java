package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genrestorage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genrestorage.GenreStorage;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> getAllGenre(){
        return genreStorage.getAllGenre();
    }

    public Genre getGenreToId(int id){
        return genreStorage.getGenreToId(id);
    }
}
