package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.directorStorage.DirectorStorage;

import java.util.List;

@Slf4j
@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(@Qualifier("directorDbStorage") DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> getDirectors() {
        log.info("Запрос на вывод всех режиссëров получен.");
        return directorStorage.findAll();
    }

    public Director getDirectorById(Integer id) {
        log.info("Запрос на вывод режиссëра с ID {} получен.", id);
        return directorStorage.findById(id).orElseThrow(
                () -> new NotFoundException("Режиссёра с id = " + id + " не существует!")
        );
    }

    public Director create(Director director) {
        log.info("Запрос на добавление режиссëра получен.");
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        log.info("Запрос на обновление режиссëра получен.");
        checkDirectorForExist(director.getId());
        return directorStorage.update(director);
    }

    public void delete(Integer id) {
        log.info("Запрос на удаление режиссëра получен.");
        checkDirectorForExist(id);
        directorStorage.delete(id);
    }

    public void checkDirectorForExist(Integer id) {
        if (!directorStorage.isDirectorExist(id)) {
            throw new NotFoundException("Режиссёра с id = " + id + " не существует!");
        }
    }
}
