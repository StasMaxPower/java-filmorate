package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    private final List<User> users = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);


    @GetMapping("/users")
    public List<User> getUsers(){
        log.info("Запрос на вывод пользователей получен.");
        return users;
    }

    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user){

        if (user.getLogin().isBlank()) {
            log.info("Логин пользователя не задан");
            throw new ValidationException("Логин пользователя не задан");
        }
        if (user.getEmail().isBlank()||(!user.getEmail().contains("@"))) {
            log.info("Неверно указана электронная почта");
            throw new ValidationException("Неверно указана электронная почта");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Неверная дата рождения");
            throw new ValidationException("Неверная дата рождения");
        }

        users.add(user);
        log.info("Запрос на добавление пользователя получен.");
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user){

        if (user.getLogin().isBlank()) {
            log.info("Логин пользователя не задан");
            throw new ValidationException("Логин пользователя не задан");
        }
        if (user.getEmail().isBlank()||(!user.getEmail().contains("@"))) {
            log.info("Неверно указана электронная почта");
            throw new ValidationException("Неверно указана электронная почта");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Неверная дата рождения");
            throw new ValidationException("Неверная дата рождения");
        }

        users.add(user);
        log.info("Запрос на добавление пользователя получен.");
        return user;
    }

}
