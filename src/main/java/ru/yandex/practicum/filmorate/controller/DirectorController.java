package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class DirectorController {
    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    //GET /directors — Список всех режиссёров
    @GetMapping("/directors")
    public List<Director> getDirectors() {
        return directorService.getDirectors();
    }

    //GET /directors/{id} — Получение режиссёра по id
    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable Integer id) {
        return directorService.getDirectorById(id);
    }

    //POST /directors` - Создание режиссёра
    @PostMapping("/directors")
    public Director create(@Valid @RequestBody Director director) {
        return directorService.create(director);
    }

    //PUT /directors` - Изменение режиссёра
    @PutMapping("/directors")
    public Director update(@Valid @RequestBody Director director) {
        return directorService.update(director);
    }

    //DELETE /directors/{id} - Удаление режиссёра
    @DeleteMapping("/directors/{id}")
    public void delete(@PathVariable Integer id) {
        directorService.delete(id);
    }
}
