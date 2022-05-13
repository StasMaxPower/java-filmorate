package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;



@Data
public class User {
    private final int id;
    @Email
    private final String email;
    @NonNull @NotBlank
    private final String login;
    private String name;
    private final LocalDate birthday;


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
}
