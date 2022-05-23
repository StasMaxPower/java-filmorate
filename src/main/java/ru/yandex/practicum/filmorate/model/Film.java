package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    @NotBlank
    private final String name;
    @Size(min = 0, max = 200)
    private final String description;
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    private Set<Integer> likes = new HashSet<>();

    public void addLike(int id){
        likes.add(id);
    }

    public void deleteLike(int id){
        likes.remove(id);
    }
}
