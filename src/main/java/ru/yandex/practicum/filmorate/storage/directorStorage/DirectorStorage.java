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
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Director> findAll() {
        String sql = "SELECT * FROM DIRECTORS ORDER BY DIRECTOR_ID;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

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

    public Director update(Director director) {
        String sql = "UPDATE DIRECTORS SET NAME = ? WHERE DIRECTOR_ID = ?;";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        log.info("Обновлен режиссёр: {} {}", director.getId(), director.getName());
        return director;
    }

    public void delete(Integer id) {
        String sql = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?;";
        jdbcTemplate.update(sql, id);
        log.info("Удален режиссёр с id: {}", id);
    }

    public boolean isDirectorExist(Integer id) {
        return findById(id).isPresent();
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return new Director(rs.getInt("director_id"), rs.getString("name"));
    }
}
