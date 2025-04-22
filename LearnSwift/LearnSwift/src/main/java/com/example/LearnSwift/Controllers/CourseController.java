package com.example.LearnSwift.Controllers;

import com.example.LearnSwift.Model.Course;
import com.example.LearnSwift.Model.Instructor;
import com.example.LearnSwift.Services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController //this is a controller class
@RequestMapping("/api/courses") //All the endpoints of this class start with this
public class CourseController {
    private final CourseService courseService;

    @Autowired //injects the object into the constructor without having to create it manually
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("/{course_id}")
    public ResponseEntity<Course> getCourseByTitle(@PathVariable Long course_id) {
        Optional<Course> course = courseService.getCourseById(course_id);
        return course.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{title}/instructors")
    public ResponseEntity<List<Instructor>> getInstructorsByCourse(@PathVariable String title) {
        List<Instructor> instructors = courseService.getInstructorsByCourse(title);
        return new ResponseEntity<>(instructors, HttpStatus.OK);
    }

    @PostMapping("/instructor/{instructor_id}")
    public ResponseEntity<String> createCourse(@RequestBody Course course, @PathVariable Long instructor_id,@RequestParam double price, @RequestParam int duration) {
        try {
            courseService.createCourse(course, instructor_id, price, duration);
            return new ResponseEntity<>("Course created successfully", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Course already exists", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);}
    }

    @DeleteMapping("/{course_id}/instructor/{instructor_id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long course_id, @PathVariable Long instructor_id) {
        try {
            courseService.deleteCourse(course_id, instructor_id);
            return new ResponseEntity<>("Course deleted successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Course not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);}
    }
}
