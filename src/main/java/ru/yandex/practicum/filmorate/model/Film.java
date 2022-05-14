package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    private final int id;
    @NotBlank
    private final String name;
    @Size(min = 0, max = 200)
    private final String description;
    private final LocalDate releaseDate;
    @Positive
    private final int duration;

}
