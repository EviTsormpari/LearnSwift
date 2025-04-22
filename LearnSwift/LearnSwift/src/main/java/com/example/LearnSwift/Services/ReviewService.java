package com.example.LearnSwift.Services;

import com.example.LearnSwift.Model.Instructor;
import com.example.LearnSwift.Model.Review;
import com.example.LearnSwift.Model.Student;
import com.example.LearnSwift.Repositories.InstructorRepository;
import com.example.LearnSwift.Repositories.ReviewRepository;
import com.example.LearnSwift.Repositories.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service //This class is a service and has all the business logic. It communicates with repositories
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;

    @Autowired //injects the object into the constructor without having to create it manually
    public ReviewService(ReviewRepository reviewRepository, InstructorRepository instructorRepository, StudentRepository studentRepository) {
        this.reviewRepository = reviewRepository;
        this.instructorRepository = instructorRepository;
        this.studentRepository = studentRepository;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Optional<Review> getReviewById(Long id) {
        //check if review exists
        boolean exists = reviewRepository.existsById(id);
        if(!exists) {
            throw new IllegalArgumentException("Review doesn't exists");
        }
        return reviewRepository.findById(id);

    }

    @Transactional //Does a rollback if an exception occurs
    public void createReview(Review review) {
        // Check if the instructor and student exist
        Instructor instructor = instructorRepository.findById(review.getInstructor().getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        Optional<Student> existingStudent = studentRepository.findByEmail(review.getStudentName());
        if (existingStudent.isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        //Check if there is already a review from the same student for the same teacher
        boolean alreadyExists = reviewRepository.existsByStudentNameAndInstructor(review.getStudentName(), review.getInstructor());

        if (alreadyExists) {
            throw new IllegalArgumentException("This student has already reviewed this instructor.");
        }

        // Data validity checks
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        if (review.getComment() == null || review.getComment().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty.");}


        review.setReview_date(new Date());
        reviewRepository.save(review);
    }

    public void deleteReview(Long id, String studentName) {
        //check if review exists
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        // Check if the student who tries to delete the review is the same student who did it.
        if (!review.getStudentName().equals(studentName)) {
            throw new IllegalArgumentException("You are not authorized to delete this review.");
        }

        reviewRepository.deleteById(id);
    }
}
