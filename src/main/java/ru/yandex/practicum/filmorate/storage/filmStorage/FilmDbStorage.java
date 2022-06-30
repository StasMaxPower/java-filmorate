package ru.yandex.practicum.filmorate.storage.filmStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.genrestorage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpaStorage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage{
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Film> getBySearch(String query, List<String> by){
        String param1 = "";
        String param2 = "";
        if (by.contains("title")&&by.size()==1){
            param1 = query;
            param2 = "%";
        }
        if (by.contains("title")&&by.contains("director")&&by.size()==2){
            param1 = query;
            param2 = query;
        }
        if (by.contains("director")&&by.size()==1){
            param1 = "%";
            param2 = query;
        }

        String sql = "select f.film_id, f.name, f.description, f.duration, f.releasedate, " +
                    " f.rating, count(l.user_id) as count_films " +
                    " from films f left join likes l on f.film_id = l.film_id " +
                    " where name LIKE '%?%' " +
               //     " and director_id=directors.director_id and directors.name = ? "+
                    " group by f.film_id, f.name, f.description, f.duration, f.releasedate, f.rating " +
                    " order by count_films desc ";
        return  jdbcTemplate.query(sql, this::mapRowToFilm,query);
    }
    @Override
    public List<Film> getPopular(int count){
        String sql = "select f.film_id, f.name, f.description, f.duration, f.releasedate, " +
                "f.rating, count(l.user_id) as count_films " +
                "from films f left join likes l on f.film_id = l.film_id " +
                "group by f.film_id, f.name, f.description, f.duration, f.releasedate, f.rating " +
                "order by count_films desc " +
                "limit ?";
        return jdbcTemplate.query(sql,(rs, rowNum) -> new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"),
                mpaStorage.getMpaToId(rs.getInt("rating")),
                genreStorage.getGenres(rs.getInt("film_id"))),
                count);
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT * FROM FILMS ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"),
                mpaStorage.getMpaToId(rs.getInt("rating")),
                genreStorage.getGenres(rs.getInt("film_id"))
        ));
    }

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("films").usingGeneratedKeyColumns("film_id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("duration", film.getDuration())
                .addValue("releasedate", film.getReleaseDate())
                .addValue("rating", film.getMpa().getId());
        Number num = jdbcInsert.executeAndReturnKey(parameters);
        film.setId(num.intValue());

        if (film.getGenres()!= null){
            String sql = "insert into FILMS_GENRE(FILM_ID, GENRE_ID) " +
                    "values ( ?,? )";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sql, film.getId(), genre.getId());
            }
        }
        log.info("Добавлен фильм: {}", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        checkFilmId(film.getId());
        String sql = " update FILMS SET NAME = ?, DESCRIPTION = ?, DURATION = ?, RELEASEDATE = ?," +
                " RATING = ? where FILM_ID = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getId()
        );
            genreStorage.updateFilmGenre(film);
        log.info("Обновлен фильм: {}", film.getName());
        return getToId(film.getId());
    }


    @Override
    public Film getToId(int id) {
        checkFilmId(id);
        String sqlQuery = "select * from films where film_ID = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery,(rs, rowNum) ->(new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releasedate").toLocalDate(),
                rs.getInt("duration"),
                mpaStorage.getMpaToId(rs.getInt("rating")),
                genreStorage.getGenres(rs.getInt("film_id")))), id);
        log.info("Вывод фильма: {}", film.getName());
        return film;
    }

    @Override
    public Film checkFilmId(int id) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from FILMS where FILM_ID = ?", id);
        if (!rows.next()) throw new NotFoundException("Нет такого фильма");
        return null;
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releasedate").toLocalDate(),
                rs.getInt("duration"),
                mpaStorage.getMpaToId(rs.getInt("rating")),
                genreStorage.getGenres(rs.getInt("film_id")));
    }
}
