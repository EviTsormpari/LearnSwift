package com.example.LearnSwift.Services;

import com.example.LearnSwift.Model.*;
import com.example.LearnSwift.Repositories.InstructorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Service //This class is a service and has all the business logic. It communicates with repositories
public class InstructorService {

    private final InstructorRepository instructorRepository;
    //private final PasswordEncoder passwordEncoder;

    @Autowired //injects the object into the constructor without having to create it manually
    public InstructorService(InstructorRepository instructorRepository, PasswordEncoder passwordEncoder) {
        this.instructorRepository = instructorRepository;
       // this.passwordEncoder = passwordEncoder;
    }

    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    public Optional<Instructor> getInstructorByUsername(String username) {
        boolean exists = instructorRepository.existsByUsername(username);
        if(!exists) {
            throw new IllegalArgumentException("Instructor doesn't exists");
        }
        return instructorRepository.findByUsername(username);
    }

    public void deleteInstructor(Long id) {
        //check if the instructor exists
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        instructorRepository.deleteById(id);
    }


    @Transactional //Does a rollback if an exception occurs
    public void updateInstructorUsername(Long id, String newUsername) {
        //check if the instructor exists
        Instructor instructor = instructorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        //check if the given username is valid
        if(newUsername != null && !newUsername.isEmpty() && !Objects.equals(instructor.getUsername(), newUsername)) {
            instructor.setUsername(newUsername);
            instructorRepository.save(instructor);
        } else {
            throw new IllegalArgumentException("Invalid username.");
        }
    }

    @Transactional //Does a rollback if an exception occurs
    public void updateInstructorPassword(Long id, String newPassword) {
        //check if the instructor exists
        Instructor instructor = instructorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        //check if the given password is valid
        if(newPassword != null && !newPassword.isEmpty() && !Objects.equals(instructor.getPassword(), newPassword)) {
            instructor.setPassword(newPassword);
            instructorRepository.save(instructor);
        } else {
            throw new IllegalArgumentException("Invalid password.");
        }
    }

    @Transactional //Does a rollback if an exception occurs
    public void updateInstructorEmail(Long id, String newEmail) {
        //check if the instructor exists
        Instructor instructor = instructorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        //check if the given email is valid
        if(newEmail != null && !newEmail.isEmpty() && !Objects.equals(instructor.getEmail(), newEmail)) {
            instructor.setEmail(newEmail);
            instructorRepository.save(instructor);
        } else {
            throw new IllegalArgumentException("Invalid email.");
        }
    }

    //LOGIN/REGISTER
    public void registerInstructor(Instructor instructor) {
        //check if the instructor exists according to username
        if (instructorRepository.existsByUsername(instructor.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        //check if the student exists according to email
        if (instructorRepository.existsByEmail(instructor.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        instructor.setPassword(instructor.getPassword());
        instructorRepository.save(instructor);
    }

    public void loginInstructor(String email, String password) {
        //check if the instructor exists
        Instructor instructor = instructorRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        if (!(instructor.getPassword().equals(password))) {
            throw new IllegalArgumentException("Invalid password");
        }

    }

    public List<Course> getCoursesByInstructor(String username) {
        //check if the instructor exists
        Instructor instructor = instructorRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        return instructor.getCourses();
    }

    public Optional<Double> getCoursePriceByInstructor(String username, Long course_id) {
        //check if the instructor exists
        Instructor instructor = instructorRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        return Optional.of(instructor.getCoursePrice(course_id));
    }

    public Optional<Integer> getCourseDurationByInstructor(String username, Long course_id) {
        //check if the instructor exists
        Instructor instructor = instructorRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        return Optional.of(instructor.getCourseDuration(course_id));
    }

    public List<Review> getReviewsByInstructor(Long id) {
        //check if the instructor exists
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        return instructor.getReviews();
    }

    public List<Booking> getBookingsByInstructor(Long id) {
        //check if the instructor exists
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        return instructor.getBookings();
    }
}
