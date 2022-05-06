package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Setter
public class User {
    int id;
    @Email String email;
    @NotNull @NotBlank String login;
    String name;
    LocalDate birthday;


    public User(int id, String email, String login, String name) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
    }
}
