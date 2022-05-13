package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private final int id;
    @NonNull @NotBlank
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final Duration duration;

}
