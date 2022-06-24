package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class User {
    private int id;
    @Email
    private  String email;
    @NotBlank
    private  String login;
    private String name;
    @Past
    private  LocalDate birthday;
    private Set<Integer> friends;

    public User( String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        if (name.isEmpty()) {
            this.name = login;
        }
        else
        this.name = name;
        this.birthday = birthday;
        friends = new HashSet<>();
    }

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        if (name.isEmpty()) {
            this.name = login;
        }
        else
            this.name = name;
        this.birthday = birthday;
    }

    public void addFriends(int id){
        friends.add(id);
    }

    public User deleteFriends(int id){
        friends.remove(id);
        return this;
    }
}
