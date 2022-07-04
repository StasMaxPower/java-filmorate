package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.reviewStorage.ReviewStorage;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;


    @Autowired
    public ReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Review createReview(Review review) {
        log.info("Запрос на создание отзыва получен");
        reviewStorage.addReview(review);
        return review;
    }
    public Review editReview(Review review) {
        log.info("Запрос на редакирование отзыва получен");
        reviewStorage.updateReview(review);
        return review;
    }



    public Review deleteReview(int id) {
        reviewStorage.deleteReview(id);
        log.info("Запрос на удаление отзыва получен");
    }
}