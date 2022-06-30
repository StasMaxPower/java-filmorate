package ru.yandex.practicum.filmorate.storage.mpaStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Component
public class MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Mpa> getMpa(){
        String sql = "select * from rating";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Mpa(rs.getInt("rating_id"), rs.getString("name")));
    }

    public Mpa getMpaToId(int id){
        checkId(id);
        String sql = "select * from rating where RATING_ID = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                new Mpa(rs.getInt("rating_id"), rs.getString("name")), id);
    }

/*    public Mpa getMpa(int id){
        String sql = "SELECT NAME FROM RATING WHERE RATING_ID = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        if (rows.next()) {
            return new Mpa(id,
                    rows.getString("name"));
        }
        else return null;
    }*/

    public void checkId(int id){
         SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from RATING where RATING_ID = ?", id);
         if (!rows.next()) throw new NotFoundException("Такого рейтинга нет");

    }
}
