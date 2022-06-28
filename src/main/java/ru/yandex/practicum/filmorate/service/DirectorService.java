package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.directorStorage.DirectorStorage;

import java.util.List;

@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> getDirectors() {
        return directorStorage.findAll();
    }

    public Director getDirectorById(Integer id) {
        return directorStorage.findById(id).orElseThrow(
                () -> new NotFoundException("Режиссёра с id = " + id + " не существует!")
        );
    }
    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        checkDirectorForExist(director.getId());
        return directorStorage.update(director);
    }

    public void delete(Integer id) {
        checkDirectorForExist(id);
        directorStorage.delete(id);
    }

    public void checkDirectorForExist(Integer id) {
        if (!directorStorage.isDirectorExist(id)) {
            throw new NotFoundException("Режиссёра с id = " + id + " не существует!");
        }
    }
}
