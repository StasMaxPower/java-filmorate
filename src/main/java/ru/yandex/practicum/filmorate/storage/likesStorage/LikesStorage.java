package ru.yandex.practicum.filmorate.storage.likesStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LikesStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    public LikesStorage(JdbcTemplate jdbcTemplate, @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
    }

    public Film addLike(int filmId, int userId) {
        String sql = "insert into LIKES(film_id, user_id) values ( ?,? )";
        jdbcTemplate.update(sql, filmId, userId);
        Film film = filmStorage.getToId(filmId);
        log.info("Добавлен лайк фильму: {}", film.getName());
        return film;
    }

    public Film deleteLike(int filmId, int userId) {
        String sql = "delete FROM likes where film_id = ? and user_id = ? ";
        filmStorage.checkFilmId(userId);
        jdbcTemplate.update(sql, filmId, userId);
        Film film =filmStorage.getToId(filmId);
        log.info("Удален лайк у фильма: {}", film.getName());
        return film;
    }

    public Map<Integer, Map<Integer, Boolean>> getAllFilmsLikes() {
        Map<Integer, Map<Integer, Boolean>> data = new HashMap<>(); //userId, filmId, isLike
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("SELECT * FROM likes");
        while (likesRows.next()) {
            int filmId = likesRows.getInt("film_id");
            int userId = likesRows.getInt("user_id");
            Map<Integer, Boolean> dataFilmLike = new HashMap<>();
            if (data.containsKey(userId)) {
                dataFilmLike = data.get(userId);
            }
            dataFilmLike.put(filmId, true);
            data.put(userId, dataFilmLike);
        }
        return data;
    }
}
