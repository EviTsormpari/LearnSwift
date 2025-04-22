package com.example.LearnSwift.Controllers;

import com.example.LearnSwift.Model.Booking;
import com.example.LearnSwift.Model.Course;
import com.example.LearnSwift.Model.Instructor;
import com.example.LearnSwift.Model.Review;
import com.example.LearnSwift.Services.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController //this is a controller class
@RequestMapping("/api/instructors") //All the endpoints of this class start with this
public class InstructorController {

    private final InstructorService instructorService;

    @Autowired //injects the object into the constructor without having to create it manually
    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    public ResponseEntity<List<Instructor>> getAllInstructors() {
        List<Instructor> instructors = instructorService.getAllInstructors();
        return new ResponseEntity<>(instructors, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Instructor> getInstructorByUsername(@PathVariable String username) {
        Optional<Instructor> instructor = instructorService.getInstructorByUsername(username);
        return instructor.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{username}/courses")
    public ResponseEntity<List<Course>> getCoursesByInstructor(@PathVariable String username) {
        List<Course> courses = instructorService.getCoursesByInstructor(username);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("/{username}/courses/{courseId}/course_price")
    public ResponseEntity<Double> getCoursePriceByInstructor(@PathVariable String username, @PathVariable Long courseId) {
        Optional<Double> price = instructorService.getCoursePriceByInstructor(username, courseId);
        return price.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{username}/courses/{courseId}/course_duration")
    public ResponseEntity<Integer> getCourseDurationByInstructor(@PathVariable String username, @PathVariable Long courseId) {
        Optional<Integer> duration = instructorService.getCourseDurationByInstructor(username, courseId);
        return duration.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{instructor_id}/reviews")
    public ResponseEntity<List<Review>> getReviewsByInstructor(@PathVariable Long instructor_id) {
        List<Review> reviews = instructorService.getReviewsByInstructor(instructor_id);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/{instructor_id}/bookings")
    public ResponseEntity<List<Booking>> getBookingsByInstructor(@PathVariable Long instructor_id) {
        List<Booking> bookings = instructorService.getBookingsByInstructor(instructor_id);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    //LOGIN/REGISTER
    @PostMapping("/register")
    public ResponseEntity<String> registerInstructor(@RequestBody Instructor instructor) {

        //Ensure the role is "instructor"
        if (!(instructor.getRole().equals("instructor"))) {
            return new ResponseEntity<>("Role must be instructor", HttpStatus.BAD_REQUEST);
        }

        try {
            instructorService.registerInstructor(instructor);
            return new ResponseEntity<>("Instructor registered successfully", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Instructor already exists", HttpStatus.CONFLICT); // Username already exists
        } catch (Exception e) {
            return new ResponseEntity<>("Error during registration", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Instructor> loginInstructor(@RequestBody Instructor instructor) {
        try {
            instructorService.loginInstructor(instructor.getEmail(), instructor.getPassword());
            return new ResponseEntity<>(instructor, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); // Invalid username or password
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{instructor_id}/username")
    public ResponseEntity<String> updateInstructorUsername(@PathVariable Long instructor_id, @RequestBody String newUsername) {
        try {
            instructorService.updateInstructorUsername(instructor_id, newUsername);
            return new ResponseEntity<>("Username updated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Instructor not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating username", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{instructor_id}/password")
    public ResponseEntity<String> updateInstructorPassword(@PathVariable Long instructor_id, @RequestBody String newPassword) {
        try {
            instructorService.updateInstructorPassword(instructor_id, newPassword);
            return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Instructor not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{instructor_id}/email")
    public ResponseEntity<String> updateInstructorEmail(@PathVariable Long instructor_id, @RequestBody String newEmail) {
        try {
            instructorService.updateInstructorEmail(instructor_id, newEmail);
            return new ResponseEntity<>("Email updated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Instructor not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{instructor_id}")
    public ResponseEntity<String> deleteInstructor(@PathVariable Long instructor_id) {
        try {
            instructorService.deleteInstructor(instructor_id);
            return new ResponseEntity<>("Instructor deleted successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Instructor not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
