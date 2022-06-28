package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    //GET /directors — Список всех режиссёров
    @GetMapping
    public List<Director> getDirectors() {
        return directorService.getDirectors();
    }

    //GET /directors/{id} — Получение режиссёра по id
    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable Integer id) {
        return directorService.getDirectorById(id);
    }

    //POST /directors` - Создание режиссёра
    @PostMapping
    public Director create(@RequestBody Director director) {
        return directorService.create(director);
    }

    //PUT /directors` - Изменение режиссёра
    @PutMapping
    public Director update(@RequestBody Director director) {
        return directorService.update(director);
    }

    //DELETE /directors/{id} - Удаление режиссёра
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        directorService.delete(id);
    }
}
