package ru.yandex.practicum.filmorate.storafe.user.UserStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage{
    private final Map<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private int userId = 0;
/*    @Override
    public User addUserToFriends(User user, int friendId){
        log.info("Запрос на добавление в друзья получен.");
            users.get(id).getFriends().add(friendId);
            return users.get(id);

    }*/

    @Override
    public Collection<User> getUsers(){
        return users.values();
    }

    @Override
    public User addUser(User user){
        checkUser(user);
        if (users.containsKey(user.getId())){
            log.info("Такой пользователь уже существует");
            throw new ValidationException("Такой пользователь уже существует");
        }
        user.setId(++userId);
        users.put(user.getId(),user);
        return user;
    }

    @Override
    public User updateUser(User user){
        checkUser(user);
        if (!users.containsKey(user.getId())){
            log.info("Ошибка обновления пользователя. Такого пользователя нет");
            throw new NotFoundException("Ошибка обновления пользователя. Такого пользователя нет");
        }
        users.put(user.getId(),user);
        return user;
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
