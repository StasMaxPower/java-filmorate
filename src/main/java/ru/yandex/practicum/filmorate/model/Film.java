package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private final int id;
    @NotBlank
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final Duration duration;

}
