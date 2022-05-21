package ru.yandex.practicum.filmorate.service;

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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storafe.user.UserStorage.UserStorage;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
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
            throw new ValidationException("Друзей нет");
        for (int i : friends){
            User user = getUserToId(i);
            users.add(user);
        }
        return users;
    }

    public User deleteUserFromFriend(int id, int friendId){
        log.info("Запрос на удаление из друзей получен.");
        getUserToId(id).getFriends().remove(friendId);
        return getUserToId(id);
    }

    public User addUserToFriend(int id, int friendId){
        getUserToId(friendId).getFriends().add(id);;
        log.info("Запрос на добавление в друзья получен.");
        getUserToId(id).getFriends().add(friendId);
        return getUserToId(id);
    }

    public User getUserToId(int id){
        log.info("Запрос на вывод пользователя по ID получен.");
        return userStorage.getUsers().stream()
                .filter(x->x.getId() == id)
                .findFirst()
                .orElseThrow(()->new NotFoundException("Нет такого юзера"));
    }

    public Collection<User> getUsers(){
        log.info("Запрос на вывод пользователей получен.");
        return userStorage.getUsers();
    }

    public User addUser(User user){
        log.info("Запрос на добавление пользователя получен.");
        return userStorage.addUser(user);
    }

    public User updateUser(User user){
        log.info("Запрос на обновление пользователя получен.");
        return userStorage.updateUser(user);
    }
}
