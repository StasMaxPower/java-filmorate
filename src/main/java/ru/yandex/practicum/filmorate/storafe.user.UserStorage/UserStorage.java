package ru.yandex.practicum.filmorate.storafe.user.UserStorage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    //public User addUserToFriends(int id, int friendId);

    public Collection<User> getUsers();

    public User addUser(User user);

    public User updateUser(User user);
}
