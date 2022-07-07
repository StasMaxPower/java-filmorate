package ru.yandex.practicum.filmorate.storage.directorStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component("directorDbStorage")
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> findAll() {
        String sql = "SELECT * FROM DIRECTORS ORDER BY DIRECTOR_ID;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Optional<Director> findById(Integer id) {
        String sql = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?;";
        List<Director> directors = jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), id);

        if (directors.isEmpty()) {
            log.info("Режиссёра с идентификатором {} не найден.", id);
            return Optional.empty();
        } else {
            Director director = directors.get(0);
            log.info("Найден режиссёр: {} {}", director.getId(), director.getName());
            return Optional.of(director);
        }
    }

    @Override
    public Director create(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO DIRECTORS (NAME) VALUES (?);";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"director_id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        log.info("Создан режиссёр: {} {}", director.getId(), director.getName());
        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE DIRECTORS SET NAME = ? WHERE DIRECTOR_ID = ?;";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        log.info("Обновлен режиссёр: {} {}", director.getId(), director.getName());
        return director;
    }

    @Override
    public void delete(Integer id) {
        String sql1 = "DELETE FROM FILM_DIRECTORS WHERE DIRECTOR_ID = ?;";
        jdbcTemplate.update(sql1, id);
        String sql2 = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?;";
        jdbcTemplate.update(sql2, id);
        log.info("Удален режиссёр с id: {}", id);
    }

    @Override
    public boolean isDirectorExist(Integer id) {
        return findById(id).isPresent();
    }

    @Override
    public Set<Director> getDirectors(int film_id) {
        String sql = "SELECT D.DIRECTOR_ID, D.NAME FROM DIRECTORS D JOIN FILM_DIRECTORS FD ON " +
                "FD.DIRECTOR_ID = D.DIRECTOR_ID WHERE FD.FILM_ID = ?";
        List<Director> result = jdbcTemplate.query(sql, (rs, rowNum) -> new Director(
                        rs.getInt("director_id"),
                        rs.getString("name")),
                film_id
        );
        Collections.sort(result, (o, o1) -> (o.getId() - o1.getId()));
        //if (result.isEmpty()) return null;
        return Set.copyOf(result);
    }

    @Override
    public void updateFilmDirector(Film film) {
        String sql = "DELETE FROM FILM_DIRECTORS WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());
        if (film.getDirectors() != null) {
            sql = "INSERT INTO FILM_DIRECTORS(FILM_ID, DIRECTOR_ID) VALUES ( ?,? )";
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(sql, film.getId(), director.getId());
            }
        }
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return new Director(rs.getInt("director_id"), rs.getString("name"));
    }
}
