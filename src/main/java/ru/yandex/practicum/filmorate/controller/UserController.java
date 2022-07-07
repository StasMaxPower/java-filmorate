package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getSameFriendsWithAnotherUser(@PathVariable int id, @PathVariable int otherId) {
        return userService.getSameFriendsWithAnotherUser(id, otherId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUsersFriends(@PathVariable int id) {
        return userService.getUsersFriends(id);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteUserFromFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.deleteUserFromFriend(id, friendId);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addUserToFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.addUserToFriend(id, friendId);
    }

    @GetMapping("/users/{id}")
    public User getUserToId(@PathVariable int id) {
        return userService.getUserToId(id);
    }

    @GetMapping("/users")
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id){
        userService.deleteUser(id);
    }

    @GetMapping("/users/{id}/recommendations")
    public List<Film> getRecommendationsForUserById(@PathVariable int id) {
        return userService.getRecommendationsForUserById(id);
    }

    @GetMapping("/users/{id}/feed")
    public List<Feed> getEventFeed(@PathVariable int id){
        return userService.getEventFeed(id);
    }
}
