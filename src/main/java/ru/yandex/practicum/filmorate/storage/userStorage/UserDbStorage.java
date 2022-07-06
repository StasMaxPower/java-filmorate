package ru.yandex.practicum.filmorate.storage.userStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feedStorage.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FeedStorage feedStorage;


    public UserDbStorage(JdbcTemplate jdbcTemplate, FeedStorage feedStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.feedStorage = feedStorage;
    }

    @Override
    public List<User> getSameFriendsWithAnotherUser(int id, int otherId){
        List<User> result = new ArrayList<>();
        User user;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select FRIEND_ID from FRIENDS" +
                " where USER_ID = ? and FRIEND_ID in (select FRIEND_ID from FRIENDS" +
                " where USER_ID = ? )", id, otherId);
        while (userRows.next()) {
            user = getToId(userRows.getInt("FRIEND_ID"));
            result.add(user);
        }
        return result;
    }

    @Override
    public List<User> getUsersFriends(int id) {
        checkId(id);
        List<User> result = new ArrayList<>();
        User user;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select FRIEND_ID from FRIENDS" +
                " where USER_ID = ?", id);
        while (userRows.next()) {
            user = getToId(userRows.getInt("FRIEND_ID"));
            result.add(user);
        }
        return result;
    }

    @Override
    public User addToFriend(int id, int friendId){
        checkId(id);
        checkId(friendId);
        User user = getToId(id);
        String sql = "insert into FRIENDS(FRIEND_ID, USER_ID, STATUS) values ( ?,?,'true')";
        jdbcTemplate.update(sql, friendId, id);
        feedStorage.addEventFeed(id, friendId, EventType.FRIEND, Operation.ADD);
        return user;
    }

    @Override
    public User deleteFromFriend(int id, int friendId) {
        String sql = "delete  from FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(sql, id, friendId);
        feedStorage.addEventFeed(id, friendId, EventType.FRIEND, Operation.REMOVE);
        return getToId(id);
    }

    @Override
    public Collection<User> getAll() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User add(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("users").usingGeneratedKeyColumns("user_id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());
        Number num = jdbcInsert.executeAndReturnKey(parameters);
        user.setId(num.intValue());
        log.info("Пользователь добавлен: {} {}", user.getId(), user.getName());
        return user;
    }

    @Override
    public User update(User user) {
        checkId(user.getId());
        String sql = " update USERS SET NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ?" +
                "where USER_ID = ?";
        jdbcTemplate.update(sql,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId()
        );
        log.info("Пользователь обновлен: {} {}", user.getId(), user.getName());
        return user;
    }

    @Override
    public User getToId(int id) {
        checkId(id);
        String sqlQuery = "select * from USERS where USER_ID = ?";
        User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        log.info("Пользователь найден: {} {}", user.getId(), user.getName());
        return user;
    }

    @Override
    public void deleteUser(int id) {
        getToId(id);
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";

        jdbcTemplate.update(sqlQuery, id);
        log.info("User with id '{}' deleted", id);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(
                resultSet.getInt("user_id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                resultSet.getDate("birthday").toLocalDate()
        );
    }
    public void checkId(int id){
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS where USER_ID = ?", id);
        if (!userRows.next()) throw new NotFoundException("Такого пользователя нет");
    }
}
