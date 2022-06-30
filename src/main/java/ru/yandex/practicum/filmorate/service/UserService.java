package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;
import java.time.LocalDate;
import java.util.*;


@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private int userId;

    @Autowired
    public UserService(@Qualifier("userDbStorage")UserStorage userStorage) {
        this.userStorage = userStorage;
        userId = 0;
    }

    public List<User> getSameFriendsWithAnotherUser(int id, int otherId){
        log.info("Запрос на вывод списка общих друзей получен.");
        return userStorage.getSameFriendsWithAnotherUser(id, otherId);
    }

    public List<User> getUsersFriends(int id){
        log.info("Запрос на вывод списка друзей получен.");
        return userStorage.getUsersFriends(id);
    }

    public User deleteUserFromFriend(int id, int friendId){
        return userStorage.deleteFromFriend(id, friendId);
    }

    public User addUserToFriend(int id, int friendId){
        log.info("Запрос на добавление в друзья получен.");
        return userStorage.addToFriend(id, friendId);
    }

    public User getUserToId(int id){
        log.info("Запрос на вывод пользователя по ID получен.");
        return userStorage.getToId(id);
    }

    public Collection<User> getUsers(){
        log.info("Запрос на вывод пользователей получен.");
        return userStorage.getAll();
    }

    public User addUser(User user){
        log.info("Запрос на добавление пользователя получен.");
        checkUser(user);
        user.setId(++userId);
        return userStorage.add(user);
    }

    public User updateUser(User user){
        checkUser(user);
        log.info("Запрос на обновление пользователя получен.");
        return userStorage.update(user);
    }

    public void checkUser(User user){
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
        if (user.getName().isEmpty())
            user.setName(user.getLogin());
    }
}
