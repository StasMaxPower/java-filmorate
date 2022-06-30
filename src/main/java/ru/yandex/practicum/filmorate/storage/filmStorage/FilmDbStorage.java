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
import ru.yandex.practicum.filmorate.storage.genrestorage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpaStorage.MpaStorage;

import java.util.*;
import java.util.stream.Collectors;

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
    public List<Film> getPopular(int count, int genreId, int year){
        String searchByGenre = "";
        String searchByYear = "";

        if (genreId != 0) searchByGenre = " = ? ";
        else searchByGenre = " > ? OR fg.genre_id IS NULL ";

        if (year != 0)  searchByYear = " = ? ";
        else searchByYear = " > ? ";

        String sql = "SELECT f.film_id, f.name, f.description, f.duration, f.releasedate, f.rating, count(l.user_id) AS count_films " +
                "FROM films AS f " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "LEFT JOIN films_genre AS fg ON f.film_id = fg.film_id " +
                "WHERE EXTRACT(YEAR FROM CAST(f.releasedate AS date)) " + searchByYear +
                "AND (fg.genre_id " + searchByGenre + ") " +
                "GROUP BY f.film_id, f.name, f.description, f.duration, f.releasedate, f.rating " +
                "ORDER BY count_films desc " +
                "LIMIT ?";
        return jdbcTemplate.query(sql,(rs, rowNum) -> new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"),
                mpaStorage.getMpaToId(rs.getInt("rating")),
                genreStorage.getGenres(rs.getInt("film_id"))),
                year, genreId, count);
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
    public void deleteFilm(int id) {
        checkFilmId(id);
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, id);
        log.info("Film with id '{}' deleted", id);
    }

    @Override
    public Film checkFilmId(int id) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from FILMS where FILM_ID = ?", id);
        if (!rows.next()) throw new NotFoundException("Нет такого фильма");
        return null;
    }
}
