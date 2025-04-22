package com.example.LearnSwift.Controllers;

import com.example.LearnSwift.Model.Booking;
import com.example.LearnSwift.Model.Student;
import com.example.LearnSwift.Services.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController //this is a controller class
@RequestMapping("/api/students") //All the endpoints of this class start with this
public class StudentController {
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/{student_username}")
    public ResponseEntity<Student> getStudentByUsername(@PathVariable String student_username) {
        Optional<Student> student = studentService.getStudentByUsername(student_username);
        return student.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{student_username}/bookings")
    public ResponseEntity<List<Booking>> getBookingsByStudent(@PathVariable String student_username) {
        try {
            List<Booking> bookings = studentService.getBookingsByStudent(student_username);
            if (bookings.isEmpty()) {
                return new ResponseEntity<>(bookings,HttpStatus.OK);  //Return OK to be able to return empty list
            }
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //LOGIN/REGISTER
    @PostMapping("/register")
    public ResponseEntity<Student> registerStudent(@RequestBody Student student) {
        //Ensure the role is "student"
        if (!(student.getRole().equals("student"))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            studentService.registerStudent(student);
            return new ResponseEntity<>(student, HttpStatus.CREATED); //Return student to pass his credentials in the next page after login
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>( HttpStatus.CONFLICT); // Username already exists
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Student> loginStudent(@RequestParam String email, @RequestParam String password) {
        try {
            Student student = studentService.loginStudent(email, password);
            return new ResponseEntity<>(student, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); // Invalid username or password
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{student_id}/username/{newUsername}")
    public ResponseEntity<String> updateStudentUsername(@PathVariable Long student_id, @PathVariable String newUsername) {
        try {
            studentService.updateStudentUsername(student_id, newUsername);
            return new ResponseEntity<>("Username updated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating username", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{student_id}/password/{newPassword}")
    public ResponseEntity<String> updateStudentPassword(@PathVariable Long student_id, @PathVariable String newPassword) {
        try {
            System.out.println(newPassword);
            studentService.updateStudentPassword(student_id, newPassword);
            return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{student_id}/email")
    public ResponseEntity<String> updateStudentEmail(@PathVariable Long student_id, @RequestBody String newEmail) {
        try {
            studentService.updateStudentEmail(student_id, newEmail);
            return new ResponseEntity<>("Email updated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{student_id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long student_id) {
        try {
            studentService.deleteStudent(student_id);
            return new ResponseEntity<>("Student deleted successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    }
}
