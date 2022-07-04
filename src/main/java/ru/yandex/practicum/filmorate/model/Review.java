package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class Review {
    private int id;
    @NotNull
    private String content;
    private boolean isPositive;
    @NotNull
    private int userId;
    @NotNull
    private int filmId;
    int useful;

    public Review(int id, String content, boolean isPositive, int userId, int filmId, int useful) {
        this.id = id;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }
}
