package ru.yandex.practicum.filmorate.storage.feedStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addEventFeed(int userId, int entityId, EventType eventType, Operation operation) {
        String sqlQuery = "INSERT INTO feed(user_id, entity_id, event_type, operation, timestamp) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                userId,
                entityId,
                eventType.toString(),
                operation.toString(),
                Instant.now().toEpochMilli());
        log.info("User id '{}' was successfully added entity id '{}' to the feed", userId, entityId);
    }

    @Override
    public List<Feed> getEventFeed(int userId) {
        String sqlQuery = "SELECT * FROM feed WHERE user_id = ?";

        return jdbcTemplate.query(sqlQuery,this::mapRowToFeed, userId);
    }

    private Feed mapRowToFeed(ResultSet resultSet, int rowNum) throws SQLException {
        return new Feed(
                resultSet.getInt("event_id"),
                resultSet.getInt("user_id"),
                resultSet.getInt("entity_id"),
                resultSet.getString("event_type"),
                resultSet.getString("operation"),
                resultSet.getLong("timestamp")
        );
    }
}
