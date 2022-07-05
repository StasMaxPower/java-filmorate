package ru.yandex.practicum.filmorate.storage.reviewStorage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review getReviewById(int id);

    Review addReview(Review review);
    Review updateReview(Review review);

    void deleteLikeToReview(Integer id, Integer userId);

    void deleteDislikeToReview(Integer id, Integer userId);

    List<Review> getAllReviews(int count);

    void deleteReview(int id);
    Review getToId(int id);
    Review checkReviewId(int id);


    List<Review> getReviewByFilmId(int filmId, int count);

    void addLikeToReview(Integer id, Integer userId);

    void addDislikeToReview(Integer id, Integer userId);
}
