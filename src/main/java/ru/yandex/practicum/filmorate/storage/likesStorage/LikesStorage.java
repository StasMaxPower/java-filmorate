package ru.yandex.practicum.filmorate.storage.likesStorage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface LikesStorage {

    public Film addLike(int filmId, int userId);

    public Film deleteLike(int filmId, int userId);

    public Map<Integer, Map<Integer, Boolean>> getAllFilmsLikes();
}
