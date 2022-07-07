package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;
@RequiredArgsConstructor
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping(value = "/reviews")
    public Review addReview(@Valid @RequestBody Review review){
        return reviewService.createReview(review);
    }

    @PutMapping(value = "/reviews")
    public Review updateReview(@Valid @RequestBody Review review){
        return reviewService.editReview(review);
    }

    @DeleteMapping(value = "/reviews/{id}")
    public void deleteReview(@PathVariable int id){
        reviewService.deleteReview(id);
    }

    @GetMapping (value = "reviews/{id}")
    public Review getReviewById(@PathVariable int id){
        return reviewService.getReview(id);
    }

    @GetMapping(value = {"/reviews"})
    public List<Review> getFilmsByDirector(@RequestParam (required = false) Integer filmId, @RequestParam(defaultValue = "10") int count){
        return reviewService.getReviewsByFilm(filmId, count);
    }
    @PutMapping(value = "/reviews/{id}/like/{userId}")
    public void addLiketoReview(@PathVariable Integer id, @PathVariable Integer userId){
        reviewService.addLikeToReview(id, userId);
    }
    @PutMapping(value = "/reviews/{id}/dislike/{userId}")
    public void addDisliketoReview(@PathVariable Integer id, @PathVariable Integer userId){
        reviewService.addDislikeToReview(id, userId);
    }
    @DeleteMapping(value = "/reviews/{id}/like/{userId}")
    public void deleteLiketoReview(@PathVariable Integer id, @PathVariable Integer userId){
        reviewService.deleteLikeToReview(id, userId);
    }
    @DeleteMapping(value = "/reviews/{id}/dislike/{userId}")
    public void deleteDisliketoReview(@PathVariable Integer id, @PathVariable Integer userId){
        reviewService.deleteDislikeToReview(id, userId);
    }



}
