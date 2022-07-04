package ru.yandex.practicum.filmorate.storage.reviewStorage;

import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewStorage {
    Review addReview(Review review);
    Review updateReview(Review review);
    void deleteReview(int id);
    Review getToId(int id);
    Review checkReviewId(int id);



}
