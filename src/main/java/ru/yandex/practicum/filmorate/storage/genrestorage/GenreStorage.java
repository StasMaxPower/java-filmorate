package ru.yandex.practicum.filmorate.storage.genrestorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public List<Genre> getGenres(int id){
        String sql = "SELECT GENRE.GENRE_ID, GENRE.NAME FROM GENRE JOIN FILMS_GENRE ON " +
                "FILMS_GENRE.GENRE_ID = GENRE.GENRE_ID WHERE FILMS_GENRE.FILM_ID = ?";
        List<Genre> result = jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("name")),
                id
        );
        Collections.sort(result,(o, o1)->(o.getId()-o1.getId()));
        if (result.isEmpty()) return null;
        return result;
    }

    public void updateFilmGenre(Film film){
        String sql = " DELETE FROM FILMS_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sql,film.getId());
        if (film.getGenres() !=null){
            Set<Genre> setWithoutRepeat = new HashSet<>(film.getGenres());
            sql = " INSERT INTO FILMS_GENRE(FILM_ID, GENRE_ID) VALUES ( ?,? )";
            for (Genre genre : setWithoutRepeat ){
                jdbcTemplate.update(sql,film.getId(), genre.getId());
            }
        }
    }

    public void checkId(int id){
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from GENRE where GENRE_ID = ?", id);
        if (!rows.next()) throw new NotFoundException("Такого жанра нет");

    }
}
