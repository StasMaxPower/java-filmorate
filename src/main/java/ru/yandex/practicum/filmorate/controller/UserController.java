package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getSameFriendsWithAnotherUser(@PathVariable int id,@PathVariable int otherId){
        return userService.getSameFriendsWithAnotherUser(id, otherId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUsersFriends(@PathVariable int id){
        return userService.getUsersFriends(id);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteUserFromFriend(@PathVariable int id,@PathVariable int friendId){
        return userService.deleteUserFromFriend(id, friendId);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addUserToFriend(@PathVariable int id,@PathVariable int friendId){
        return userService.addUserToFriend(id, friendId);
    }

    @GetMapping("/users/{id}")
    public User getUserToId(@PathVariable int id){
        return userService.getUserToId(id);
    }

    @GetMapping("/users")
    public Collection<User> getUsers(){
        return userService.getUsers();
    }

    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user){
        return userService.addUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user){
        return userService.updateUser(user);
    }


}
