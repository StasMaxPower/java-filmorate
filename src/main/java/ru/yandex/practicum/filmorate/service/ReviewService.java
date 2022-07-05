package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.reviewStorage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserDbStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserDbStorage userDbStorage;


    @Autowired
    public ReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage, @Qualifier("filmDbStorage") FilmStorage filmStorage, UserDbStorage userDbStorage) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userDbStorage = userDbStorage;
    }

    public Review createReview(Review review) {
        log.info("Запрос на создание отзыва получен");
        if (review.getFilmId()==null||review.getUserId()==null||review.getIsPositive()==null) throw new ValidationException("Запрос содержит недопустимые нулевые значения");
        userDbStorage.checkId(review.getUserId());
        filmStorage.checkFilmId(review.getFilmId());

        reviewStorage.addReview(review);
        return review;
    }

    public Review editReview(Review review) {
        log.info("Запрос на редакирование отзыва получен");
        reviewStorage.checkReviewId(review.getReviewId());
        userDbStorage.checkId(review.getUserId());
        return reviewStorage.updateReview(review);
    }

    public Review getReview(int id) {
        log.info("Запрос на вывод  отзыва получен");
        reviewStorage.checkReviewId(id);
        return reviewStorage.getReviewById(id);
    }

    public void deleteReview(int id) {
        log.info("Запрос на удаление отзыва получен");
        reviewStorage.checkReviewId(id);
        reviewStorage.deleteReview(id);
    }

    public List<Review> getReviewsByFilm(Integer filmId, int count) {
        log.info("Запрос на получение отзыва получен");

        if (filmId == null) {
            return reviewStorage.getAllReviews(count);
        } else {
            filmStorage.checkFilmId(filmId);
            return reviewStorage.getReviewByFilmId(filmId, count);
        }
    }

    public void addLikeToReview(Integer id, Integer userId) {
        reviewStorage.addLikeToReview(id, userId);
    }

    public void addDislikeToReview(Integer id, Integer userId) {
        reviewStorage.addDislikeToReview(id, userId);
    }

    public void deleteLikeToReview(Integer id, Integer userId) {
        reviewStorage.checkUserHasLike(id, userId);
        reviewStorage.deleteLikeToReview(id, userId);
    }

    public void deleteDislikeToReview(Integer id, Integer userId) {
        reviewStorage.checkUserHasDislike(id, userId);
        reviewStorage.deleteDislikeToReview(id, userId);
    }
}