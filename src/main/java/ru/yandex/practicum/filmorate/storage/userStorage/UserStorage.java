package ru.yandex.practicum.filmorate.storage.userStorage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface    UserStorage {

    public List<User> getSameFriendsWithAnotherUser(int id, int otherId);
    public User addToFriend(int id, int friendId);
    public List<User> getUsersFriends(int id);
    public User deleteFromFriend(int id, int friendId);

    public Collection<User> getAll();

    public User add(User user);

    public User update(User user);

    public User getToId(int id);

    void deleteUser(int id);

}
