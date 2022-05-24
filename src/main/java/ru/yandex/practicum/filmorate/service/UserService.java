package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storafe.user.UserStorage.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private int userId;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
        userId = 0;
    }

    public List<User> getSameFriendsWithAnotherUser(int id, int otherId){
        log.info("Запрос на вывод списка общих друзей получен.");
        Set<Integer> friends = getUserToId(id).getFriends();
        Set<Integer> OtherIdFriends = getUserToId(otherId).getFriends();
        return friends.stream()
                .filter(x->OtherIdFriends.contains(x))
                .map(x->getUserToId(x))
                .collect(Collectors.toList());
    }

    public ArrayList<User> getUsersFriends(int id){
        log.info("Запрос на вывод списка друзей получен.");
        Set<Integer> friends = getUserToId(id).getFriends();
        ArrayList<User> users = new ArrayList<>();
        if (friends == null)
            throw new NotFoundException("Друзей нет");
        for (int i : friends){
            User user = getUserToId(i);
            users.add(user);
        }
        return users;
    }

    public User deleteUserFromFriend(int id, int friendId){
        return userStorage.deleteUserFromFriend(id, friendId);
    }

    public User addUserToFriend(int id, int friendId){
        getUserToId(friendId).addFriends(id);
        log.info("Запрос на добавление в друзья получен.");
        getUserToId(id).addFriends(friendId);
        return getUserToId(id);
    }

    public User getUserToId(int id){
        log.info("Запрос на вывод пользователя по ID получен.");
        return userStorage.getUserToId(id);
    }

    public Collection<User> getUsers(){
        log.info("Запрос на вывод пользователей получен.");
        return userStorage.getUsers();
    }

    public User addUser(User user){
        log.info("Запрос на добавление пользователя получен.");
        checkUser(user);
        user.setId(++userId);
        return userStorage.addUser(user);
    }

    public User updateUser(User user){
        checkUser(user);
        log.info("Запрос на обновление пользователя получен.");
        return userStorage.updateUser(user);
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
    }
}
