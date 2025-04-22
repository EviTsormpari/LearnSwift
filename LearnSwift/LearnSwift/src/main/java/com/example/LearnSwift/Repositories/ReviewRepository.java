package com.example.LearnSwift.Repositories;

import com.example.LearnSwift.Model.Instructor;
import com.example.LearnSwift.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //It provides an interface for interacting with the database
public interface ReviewRepository extends JpaRepository<Review, Long> {
    //custom method
    boolean existsByStudentNameAndInstructor(String studentName, Instructor instructor);
}
