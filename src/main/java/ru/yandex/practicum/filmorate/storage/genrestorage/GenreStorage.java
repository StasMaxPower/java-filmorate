package ru.yandex.practicum.filmorate.storage.genrestorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Component
public class GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getAllGenre(){
        String sql = "select * from genre";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("name")));
    }

    public Genre getGenreToId(int id){
        checkId(id);
        String sql = "select * from genre where GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("name")), id);
    }

    public void checkId(int id){
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from GENRE where GENRE_ID = ?", id);
        if (!rows.next()) throw new NotFoundException("Такого жанра нет");

    }
}
