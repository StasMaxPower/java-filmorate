package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feedStorage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likesStorage.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.likesStorage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;
import java.time.LocalDate;
import java.util.*;


@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final LikesStorage likesStorage;
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;
    private int userId;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       LikesDbStorage likesStorage,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage,
                       FeedStorage feedStorage) {
        this.userStorage = userStorage;
        this.likesStorage = likesStorage;
        this.filmStorage = filmStorage;
        this.feedStorage = feedStorage;
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

    public void deleteUser(int id) {
        log.info("Delete request received");
        userStorage.deleteUser(id);
    }

    public List<Feed> getEventFeed(int id) {
        log.info("Event feed request received");
        return feedStorage.getEventFeed(id);
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

    public List<Film> getRecommendationsForUserById(int id) {
        log.info("Запрос на вывод рекоммендаций для пользователя по ID получен.");

        //получаем данные по всем лайкам
        Map<Integer, Map<Integer, Boolean>> data = likesStorage.getAllFilmsLikes(); //userId, filmId, isLike

        //считаем совпадения
        Map<Integer, Integer> count = new HashMap<>();
        if (data.containsKey(id)) { //есть ли наш пользователь в таблице, иначе совпадени нет
            for (Integer i : data.keySet()) {
                if (id != i) { //не ведем подсчет по нашему пользователю
                    for (Integer j : data.get(i).keySet()) {
                        if (data.get(id).containsKey(j))
                            count.put(i, count.getOrDefault(i, 0) + 1);
                    }
                }
            }
            //находим максимальное кол-во пересечений
            int max = 0;
            for (Integer i : count.values()) {
                if (max < i) max = i;
            }
            //идем по списку пользователей с которыми было максимальное пересечение
            Set<Film> filmsWithoutLike = new HashSet<>();
            for (Integer i : count.keySet()) {
                if (count.get(i) == max) {
                    //и формируем список фильмов, где наш юзер не поставил лайк
                    Map<Integer, Boolean> filmsWithLikeUser = data.get(id);
                    Map<Integer, Boolean> filmsWithLikeFriend = data.get(i);
                    for (Integer filmId : filmsWithLikeFriend.keySet()) {
                        if (!filmsWithLikeUser.containsKey(filmId)) filmsWithoutLike.add(filmStorage.getToId(filmId));
                    }
                }
            }
            return new ArrayList<>(filmsWithoutLike);
        }
        return new ArrayList<>();
    }
}
