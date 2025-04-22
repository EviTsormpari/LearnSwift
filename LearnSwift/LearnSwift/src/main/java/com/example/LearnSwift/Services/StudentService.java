package com.example.LearnSwift.Services;

import com.example.LearnSwift.Model.Booking;
import com.example.LearnSwift.Model.Student;
import com.example.LearnSwift.Repositories.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service //This class is a service and has all the business logic. It communicates with repositories
public class StudentService {

    private final StudentRepository studentRepository;
    //private final PasswordEncoder passwordEncoder;

    @Autowired //injects the object into the constructor without having to create it manually
    public StudentService(StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
       // this.passwordEncoder = passwordEncoder;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentByUsername(String username) {
        //check if the student exists
        boolean exists = studentRepository.existsByUsername(username);
        if(!exists) {
            throw new IllegalArgumentException("Student doesn't exists");
        }
        return studentRepository.findByUsername(username);
    }

    public void deleteStudent(Long id) {
        //check if the student exists
        Student student = studentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Student not found"));

        studentRepository.deleteById(id);
    }

    @Transactional //Does a rollback if an exception occurs
    public void updateStudentUsername(Long id, String newUsername) {
        //check if the student exists
        Student student = studentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Student not found"));

        //checks for valid username
        if(newUsername != null && !newUsername.isEmpty() && !Objects.equals(student.getUsername(), newUsername)) {
            student.setUsername(newUsername);
            studentRepository.save(student);
        } else {
            throw new IllegalArgumentException("Invalid username.");
        }
    }

    @Transactional //Does a rollback if an exception occurs
    public void updateStudentPassword(Long id, String newPassword) {
        //check if the student exists
        Student student = studentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Student not found"));

        //checks for valid password
        if(newPassword != null && !newPassword.isEmpty() && !Objects.equals(student.getPassword(), newPassword)) {
            student.setPassword(newPassword);
            studentRepository.save(student);
            System.out.println("THIS IS THE NEW PASSWORD: " + student.getPassword());
        } else {
            throw new IllegalArgumentException("Invalid password.");
        }
    }

    @Transactional //Does a rollback if an exception occurs
    public void updateStudentEmail(Long id, String newEmail) {
        //check if the student exists
        Student student = studentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Student not found"));

        //checks for valid email
        if(newEmail != null && !newEmail.isEmpty() && !Objects.equals(student.getEmail(), newEmail)) {
            student.setEmail(newEmail);
            studentRepository.save(student);
        } else {
            throw new IllegalArgumentException("Invalid email.");
        }
    }

    //LOGIN/REGISTER
    public void registerStudent(Student student) {
        //check if the student exists according to username
        if (studentRepository.existsByUsername(student.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        //check if the student exists according to email
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        student.setPassword(student.getPassword());
        studentRepository.save(student);
    }

    public Student loginStudent(String email, String password) {
        //check if the student exists
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        if (!(student.getPassword().equals(password))) {
            throw new IllegalArgumentException("Invalid password");
        }

        return student;
    }

    public List<Booking> getBookingsByStudent(String username) {
        //check if the student exists
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        return student.getBooking_history();
    }
}
