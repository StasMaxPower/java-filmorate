package ru.yandex.practicum.filmorate.storage.FilmStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.UserStorage.UserStorage;

import java.util.*;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage{
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addLike( int filmId,  int userId){
        String sql = "insert into LIKES(film_id, user_id) values ( ?,? )";
        jdbcTemplate.update(sql, filmId, userId);
        return getFilmToId(filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count){
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
                getMpa(rs.getInt("rating")),
                getGenres(rs.getInt("film_id"))),
                count);
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        String sql = "delete FROM likes where film_id = ? and user_id = ? ";
        checkFilmId(userId);
        jdbcTemplate.update(sql, filmId, userId);
        return getFilmToId(filmId);
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = "SELECT * FROM FILMS ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"),
                getMpa(rs.getInt("rating")),
                getGenres(rs.getInt("film_id"))
        ));
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


    public Mpa getMpa(int id){
        String sql = "SELECT NAME FROM RATING WHERE RATING_ID = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        if (rows.next()) {
            return new Mpa(id,
                    rows.getString("name"));
        }
        else return null;
    }

    @Override
    public Film addFilm(Film film) {
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
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
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
            updateFilmGenre(film);
        return getFilmToId(film.getId());
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

    @Override
    public Film getFilmToId(int id) {
        checkFilmId(id);
        String sqlQuery = "select * from films where film_ID = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery,(rs, rowNum) ->(new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releasedate").toLocalDate(),
                rs.getInt("duration"),
                getMpa(rs.getInt("rating")),
                getGenres(rs.getInt("film_id")))), id);
        return film;
    }

    @Override
    public Film checkFilmId(int id) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from FILMS where FILM_ID = ?", id);
        if (!rows.next()) throw new NotFoundException("Нет такого фильма");
        return null;
    }
}
