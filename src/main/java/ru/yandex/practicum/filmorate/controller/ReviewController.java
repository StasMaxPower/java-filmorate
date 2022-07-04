package ru.yandex.practicum.filmorate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping(value = "/reviews")
    public Review addReview(@Valid @RequestBody Review review){
        return reviewService.createReview(review);
    }
    @PutMapping(value = "/reviews")
    public Review updateReview(@Valid @RequestBody Review review){
        return reviewService.editReview(review);
    }

    @DeleteMapping(value = "/reviews/{id}")
    public Review deleteReview(@PathVariable int id){
        return reviewService.deleteReview(id);
    }



}
