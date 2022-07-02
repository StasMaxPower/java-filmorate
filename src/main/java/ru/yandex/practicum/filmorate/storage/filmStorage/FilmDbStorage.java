package ru.yandex.practicum.filmorate.storage.filmStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.directorStorage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genrestorage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpaStorage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, MpaStorage mpaStorage, DirectorStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.directorStorage = directorStorage;
    }

    @Override
    public List<Film> getBySearch(String query, List<String> by){
        String param1 = "";
        String param2 = "";
        if (by.contains("title")&&by.size()==1){
            param1 ="'%" + query + "%'";
            param2 = "'%'";
        }
        if (by.contains("title")&&by.contains("director")&&by.size()==2){
            param1 = "'" + query + "'";
            param2 = "'" + query + "'";
        }
        if (by.contains("director")&&by.size()==1){
            param1 = "'%'";
            param2 = "'" + query + "'";
        }

        String sql = "select f.film_id, f.name, f.description, f.duration, f.releasedate, " +
                    " f.rating, count(l.user_id) as count_films " +
                    " from films f left join likes l on f.film_id = l.film_id " +
                    " left join FILM_DIRECTORS on f.FILM_ID = FILM_DIRECTORS.FILM_ID " +
                    " left join DIRECTORS on FILM_DIRECTORS.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID "+
                    " where f.name LIKE ? and directors.name LIKE ?  " +
                    " group by f.film_id, f.name, f.description, f.duration, f.releasedate, f.rating " +
                    " order by count_films desc ";
        return  jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"),
                mpaStorage.getMpaToId(rs.getInt("rating")),
                genreStorage.getGenres(rs.getInt("film_id")),
                directorStorage.getDirectors(rs.getInt("film_id"))),param1, param2);
    }
    @Override
    public List<Film>  getCommon(int userId,int friendId){
        String sql = "SELECT f.film_id, f.name, f.description, f.duration, f.releasedate, f.rating, count(l.user_id) AS count_films " +
                "FROM films AS f " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "WHERE l.user_id = ? and f.film_id in " +
                "(select films.film_id from films, likes where films.film_id = likes.film_id and likes.user_id = ?) " +
                "GROUP BY f.film_id, f.name, f.description, f.duration, f.releasedate, f.rating " +
                "ORDER BY count_films desc ";
        return jdbcTemplate.query(sql,(rs, rowNum) -> new Film(
                        rs.getInt("film_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("releaseDate").toLocalDate(),
                        rs.getInt("duration"),
                        mpaStorage.getMpaToId(rs.getInt("rating")),
                        genreStorage.getGenres(rs.getInt("film_id")),
                        directorStorage.getDirectors(rs.getInt("film_id"))),
                userId, friendId);
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
                genreStorage.getGenres(rs.getInt("film_id")),
                directorStorage.getDirectors(rs.getInt("film_id"))),
                year, genreId, count);
    }

    @Override
    public List<Film> getfindByDirector(int directorId) {
        String sql = "SELECT * FROM FILMS F " +
                "JOIN FILM_DIRECTORS FD ON FD.FILM_ID = F.FILM_ID " +
                "WHERE FD.DIRECTOR_ID = ? ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> (new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"),
                mpaStorage.getMpaToId(rs.getInt("rating")),
                genreStorage.getGenres(rs.getInt("film_id")),
                directorStorage.getDirectors(rs.getInt("film_id")))), directorId
        );
    }

    ;

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
                genreStorage.getGenres(rs.getInt("film_id")),
                directorStorage.getDirectors(rs.getInt("film_id"))
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

        if (film.getGenres() != null) {
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
        directorStorage.updateFilmDirector(film);
        log.info("Обновлен фильм: {}", film.getName());
        return getToId(film.getId());
    }


    @Override
    public Film getToId(int id) {
        checkFilmId(id);
        String sqlQuery = "select * from films where film_ID = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> (new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releasedate").toLocalDate(),
                rs.getInt("duration"),
                mpaStorage.getMpaToId(rs.getInt("rating")),
                genreStorage.getGenres(rs.getInt("film_id")),
                directorStorage.getDirectors(rs.getInt("film_id")))), id);
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
