package com.example.LearnSwift.Services;

import com.example.LearnSwift.Model.Instructor;
import com.example.LearnSwift.Repositories.CourseRepository;
import com.example.LearnSwift.Model.Course;
import com.example.LearnSwift.Repositories.InstructorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service //This class is a service and has all the business logic. It communicates with repositories
public class CourseService {
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;

    @Autowired //injects the object into the constructor without having to create it manually
    public CourseService(CourseRepository courseRepository, InstructorRepository instructorRepository) {
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long course_id) {
        Optional<Course> course = courseRepository.findById(course_id);
        if (course.isEmpty()) {
            throw new IllegalArgumentException("Course doesn't exist");
        }
        return course;
    }

    public void createCourse(Course course, Long instructorId, Double price, int duration) {

        //check if the instructor exists
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        //check if the instructor teaches this course
        if (instructor.getCourses().contains(course)) {
            throw new IllegalArgumentException("The instructor already teaches this course.");
        }


        courseRepository.save(course);
    }

    public void deleteCourse(Long courseId, Long instructorId) {
        // check if the course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        // check if the instructor teaches this course
        if (!instructor.getCourses().contains(course)) {
            throw new IllegalArgumentException("This course is not taught by the instructor");
        }

        courseRepository.deleteById(courseId);
    }

    public List<Instructor> getInstructorsByCourse(String title) {
        Course course = courseRepository.findByTitle(title).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        return course.getInstructors();
    }

}
