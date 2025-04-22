package com.example.LearnSwift.Repositories;

import com.example.LearnSwift.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository //It provides an interface for interacting with the database
public interface CourseRepository extends JpaRepository<Course, Long> {
    //custom method
    Optional<Course> findByTitle(String title);
}
