package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.directorStorage.DirectorStorage;

import java.util.List;

@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    public static void checkDirector(Director director) {
        String name = director.getName();
        if (name == null || name.isBlank()) {
            throw new ValidationException("Имя режиссëра не может быть пустым!");
        }
    }

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
        checkDirector(director);
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        checkDirector(director);
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
