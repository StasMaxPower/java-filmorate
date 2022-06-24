package ru.yandex.practicum.filmorate.storage.UserStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage{
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getSameFriendsWithAnotherUser(int id, int otherId) {
        return null;
    }

    @Override
    public User addUserToFriend(int id, int friendId) {
        return null;
    }

    @Override
    public List<User> getUsersFriends(int id) {
        return null;
    }

    @Override
    public User deleteUserFromFriend(int id, int friendId){
        return getUserToId(id).deleteFriends(friendId);
    }

    @Override
    public Collection<User> getUsers(){
        return users.values();
    }

    @Override
    public User addUser(User user){
        if (users.containsKey(user.getId())){
            log.info("Такой пользователь уже существует");
            throw new ValidationException("Такой пользователь уже существует");
        }
        users.put(user.getId(),user);
        return user;
    }

    @Override
    public User updateUser(User user){
        checkId(user.getId());
        users.put(user.getId(),user);
        return user;
    }

    @Override
    public User getUserToId(int id){
        return users.values().stream()
                .filter(x->x.getId() == id)
                .findFirst()
                .orElseGet(()->checkId(id));
    }

    public User checkId(int id){
        if (!users.containsKey(id)){
            log.info("Нет такого пользователя");
            throw new NotFoundException("Нет такого пользователя");
        }
        return users.get(id);

    }
}
