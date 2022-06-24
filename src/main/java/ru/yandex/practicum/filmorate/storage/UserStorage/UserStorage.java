package ru.yandex.practicum.filmorate.storage.UserStorage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    public List<User> getSameFriendsWithAnotherUser(int id, int otherId);
    public User addUserToFriend(int id, int friendId);
    public List<User> getUsersFriends(int id);
    public User deleteUserFromFriend(int id, int friendId);

    public Collection<User> getUsers();

    public User addUser(User user);

    public User updateUser(User user);

    public User getUserToId(int id);
}
