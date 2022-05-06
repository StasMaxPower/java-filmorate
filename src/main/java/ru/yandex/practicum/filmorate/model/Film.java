package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Film {
    int id;
    @NotNull @NotBlank String name;
    String description;
    LocalDate releaseDate;
    Duration duration;




}
