package com.example.LearnSwift.Controllers;

import com.example.LearnSwift.Model.Review;
import com.example.LearnSwift.Services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller //this is a controller class
@RequestMapping("/api/reviews") //All the endpoints of this class start with this
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired //injects the object into the constructor without having to create it manually
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/{review_id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long review_id) {
        Optional<Review> review = reviewService.getReviewById(review_id);
        return review.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<String> createReview(@RequestBody Review review) {
        try {
            reviewService.createReview(review);
            return new ResponseEntity<>("Review created successfully", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Review already exists", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);}
    }

    @DeleteMapping("/{student_name}/student")
    public ResponseEntity<String> deleteReview(@PathVariable Long review_id, @PathVariable String studentName) {
        try {
            reviewService.deleteReview(review_id, studentName);
            return new ResponseEntity<>("Review deleted successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Review not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);}
    }
}
