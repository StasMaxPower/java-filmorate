package ru.yandex.practicum.filmorate.storage.reviewStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feedStorage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserDbStorage;

import java.util.List;

@Slf4j
@Component
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FeedStorage feedStorage;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate,
                           @Qualifier("filmDbStorage") FilmStorage filmStorage,
                           UserDbStorage userStorage,
                           FeedStorage feedStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.feedStorage = feedStorage;
    }

    @Override
    public Review addReview(Review review) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("reviews").usingGeneratedKeyColumns("review_id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("is_positive", review.getIsPositive())
                .addValue("filmId", review.getFilmId())
                .addValue("userId", review.getUserId())
                .addValue("useful", 0);
        Number num = jdbcInsert.executeAndReturnKey(parameters);
        review.setReviewId(num.intValue());

        feedStorage.addEventFeed(review.getUserId(),
                review.getReviewId(),
                EventType.REVIEW,
                Operation.ADD);
        log.info("Добавлен отзыв: {}", review.getReviewId());

        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = " update REVIEWS SET CONTENT = ?, IS_POSITIVE = ? where REVIEW_ID = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );

        Review reviewUpdated = getReviewById(review.getReviewId());

        feedStorage.addEventFeed(reviewUpdated.getUserId(),
                reviewUpdated.getReviewId(),
                EventType.REVIEW,
                Operation.UPDATE);
        log.info("Обновлен отзыв: {}", review.getReviewId());

        return getToId(review.getReviewId());
    }

    @Override
    public Review getReviewById(int id) {
        String sql = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Review(
                rs.getInt("review_id"),
                rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getInt("user_id"),
                rs.getInt("film_id"),
                rs.getInt("useful")), id);
    }

    @Override
    public List<Review> getReviewByFilmId(int filmId, int count) {
        String sqlQuery = "select * from reviews where FILM_ID = ? ORDER BY USEFUL DESC limit ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> (new Review(
                rs.getInt("review_id"),
                rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getInt("user_id"),
                rs.getInt("film_id"),
                rs.getInt("useful"))), filmId, count);
    }

    @Override
    public void addLikeToReview(Integer id, Integer userId) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("review_likes").usingGeneratedKeyColumns("REVIEW_ID, USER_ID");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("reviewId", id)
                .addValue("userId", userId)
                .addValue("isLike", true);
        jdbcInsert.execute(parameters);
        String sql = "update REVIEWS SET USEFUL = USEFUL+1 where REVIEW_ID = ?";
        jdbcTemplate.update(sql, id);
        log.info("Добавлен лайк: {}", getReviewById(id).getReviewId());
    }

    @Override
    public void addDislikeToReview(Integer id, Integer userId) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("review_likes").usingGeneratedKeyColumns("REVIEW_ID, USER_ID");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("reviewId", id)
                .addValue("userId", userId)
                .addValue("isLike", false);
        jdbcInsert.execute(parameters);
        String sql = "update REVIEWS SET USEFUL = USEFUL-1 where REVIEW_ID = ?";
        jdbcTemplate.update(sql, id);
        log.info("Добавлен лайк: {}", getReviewById(id).getReviewId());
    }

    @Override
    public void checkUserHasLike(int id, int userId) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * " +
                "from REVIEW_LIKES " +
                "where REVIEW_ID = ? " +
                "   and USER_ID = ? " +
                "   and IS_LIKE = true", id, userId);
        if (!rows.next()) throw new NotFoundException("Пользователь не ставил лайк данному отзыву");
    }

    @Override
    public void checkUserHasDislike(int id, int userId) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * " +
                "from REVIEW_LIKES " +
                "where REVIEW_ID = ? " +
                "   and USER_ID = ? " +
                "   and IS_LIKE = false", id, userId);
        if (!rows.next()) throw new NotFoundException("Пользователь не ставил дизлайк данному отзыву");
    }

    @Override
    public void deleteLikeToReview(Integer id, Integer userId) {
        String sql = "delete FROM review_likes " +
                "where review_id = ? and user_id = ?;" +
                "update REVIEWS SET USEFUL = USEFUL-1 where REVIEW_ID = ?;";
        jdbcTemplate.update(sql, id, userId, id);
        log.info("Удален  лайк: {}", getReviewById(id).getReviewId());
    }

    @Override
    public void deleteDislikeToReview(Integer id, Integer userId) {
        String sql = "delete FROM review_likes " +
                "where review_id = ? " +
                "   and user_id = ?;" +
                "update REVIEWS SET USEFUL = USEFUL+1 where REVIEW_ID = ?;";
        jdbcTemplate.update(sql, id, userId, id);
        log.info("Удален  дизлайк: {}", getReviewById(id).getReviewId());
    }

    @Override
    public List<Review> getAllReviews(int count) {
        String sqlQuery = "select * from reviews  ORDER BY USEFUL DESC limit ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> (new Review(
                rs.getInt("review_id"),
                rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getInt("user_id"),
                rs.getInt("film_id"),
                rs.getInt("useful"))), count);
    }


    @Override
    public void deleteReview(int id) {
        Review review = getReviewById(id);

        String sql = "delete FROM reviews where review_id = ?";
        jdbcTemplate.update(sql, id);

        feedStorage.addEventFeed(review.getUserId(),
                review.getReviewId(),
                EventType.REVIEW,
                Operation.REMOVE);

        log.info("Удален отзыв: {}", id);
    }

    @Override
    public Review getToId(int id) {
        checkReviewId(id);
        String sqlQuery = "select * from reviews where REVIEW_ID = ?";
        Review review = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> (new Review(
                rs.getInt("review_id"),
                rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getInt("film_id"),
                rs.getInt("user_id"),
                rs.getInt("useful"))), id);
        if (review != null) {
            log.info("Вывод отзыва: {}", review.getContent());
        }
        return review;
    }

    @Override
    public Review checkReviewId(int id) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from REVIEWS where REVIEW_ID = ?", id);
        if (!rows.next()) throw new NotFoundException("Нет отзыва с таким ID");
        return null;
    }

}
