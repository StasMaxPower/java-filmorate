package ru.yandex.practicum.filmorate.storage.reviewStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

@Slf4j
@Component
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review addReview(Review review) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("reviews").usingGeneratedKeyColumns("review_id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("is_positive", review.isPositive())
                .addValue("filmId", review.getFilmId())
                .addValue("userId", review.getUserId())
                .addValue("useful", 0);
        Number num = jdbcInsert.executeAndReturnKey(parameters);
        review.setId(num.intValue());

        log.info("Добавлен отзыв: {}", review.getId());
        return review;
    }



    @Override
    public Review updateReview(Review review) {
        checkReviewId(review.getId());
        String sql = " update REVIEWS SET CONTENT = ?, IS_POSITIVE = ?, FILM_ID = ?, USER_ID = ?," +
                " USEFUL = ? where REVIEW_ID = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.isPositive(),
                review.getFilmId(),
                review.getUserId(),
                review.getUseful(),
                review.getId()
        );

        log.info("Обновлен отзыв: {}", review.getId());
        return getToId(review.getId());
    }

    @Override
    public Review getToId(int id) {
        checkReviewId(id);
        String sqlQuery = "select * from reviews where REVIEW_ID = ?";
        Review review = jdbcTemplate.queryForObject(sqlQuery,(rs, rowNum) ->(new Review(
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



    @Override
    public void deleteReview(int id) {
            checkReviewId(id);
            String sqlQuery = "DELETE * from reviews where REVIEW_ID = ?";
            Review review = jdbcTemplate.queryForObject(sqlQuery,(rs, rowNum) ->(new Review(
                    rs.getInt("review_id"),
                    rs.getString("content"),
                    rs.getBoolean("is_positive"),
                    rs.getInt("film_id"),
                    rs.getInt("user_id"),
                    rs.getInt("useful"))), id);



    }


}
