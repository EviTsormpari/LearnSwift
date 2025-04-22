package com.example.LearnSwift.Configurations;

import com.example.LearnSwift.Model.*;
import com.example.LearnSwift.Repositories.BookingRepository;
import com.example.LearnSwift.Repositories.CourseRepository;
import com.example.LearnSwift.Repositories.InstructorRepository;
import com.example.LearnSwift.Repositories.StudentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

//this class initializes some data
@Configuration
public class InitializeConfig {

    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    private final BookingRepository bookingRepository;

    public InitializeConfig(InstructorRepository instructorRepository, CourseRepository courseRepository, StudentRepository studentRepository, BookingRepository bookingRepository) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.bookingRepository = bookingRepository;
    }

    @PostConstruct
    public void initializeData() {
        //Create courses
        Course course1 = new Course();
        course1.setTitle("Mathematics");

        Course course2 = new Course();
        course2.setTitle("Physics");

        Course course3 = new Course();
        course3.setTitle("Computer Science");

        Course course4 = new Course();
        course4.setTitle("History");

        Course course5 = new Course();
        course5.setTitle("Poetry Analysis");

        //save courses
        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
        courseRepository.save(course4);
        courseRepository.save(course5);

        //Create instructor
        Instructor instructor1 = new Instructor();
        instructor1.setUsername("John B");
        instructor1.setEmail("john@example.com");
        instructor1.setRole("instructor");
        instructor1.setPassword("1224");

        Instructor instructor2 = new Instructor();
        instructor2.setUsername("Sara Cameron");
        instructor2.setEmail("sara@example.com");
        instructor2.setRole("instructor");
        instructor2.setPassword("1224");

        Instructor instructor3 = new Instructor();
        instructor3.setUsername("Rafe Stark");
        instructor3.setEmail("rafe@example.com");
        instructor3.setRole("instructor");
        instructor3.setPassword("1224");

        Instructor instructor4 = new Instructor();
        instructor4.setUsername("Kiara Belly");
        instructor4.setEmail("kiara@example.com");
        instructor4.setRole("instructor");
        instructor4.setPassword("1224");

        Instructor instructor5 = new Instructor();
        instructor5.setUsername("Topper North");
        instructor5.setEmail("topper@example.com");
        instructor5.setRole("instructor");
        instructor5.setPassword("1224");

        //save instructors
        instructorRepository.save(instructor1);
        instructorRepository.save(instructor2);
        instructorRepository.save(instructor3);
        instructorRepository.save(instructor4);
        instructorRepository.save(instructor5);

        // Assign Courses to Instructors with Relationship Update
        instructor1.addCourse(course4, 32.5, 60);
        course4.addInstructor(instructor1); // Update course side

        instructor2.addCourse(course1, 40.00, 120);
        instructor2.addCourse(course3, 25.80, 45);
        course1.addInstructor(instructor2);
        course3.addInstructor(instructor2);

        instructor3.addCourse(course1, 50.00, 90);
        instructor3.addCourse(course2, 33.00, 60);
        instructor3.addCourse(course3, 42.90, 90);
        course1.addInstructor(instructor3);
        course2.addInstructor(instructor3);
        course3.addInstructor(instructor3);

        instructor4.addCourse(course4, 20.00, 60);
        instructor4.addCourse(course5, 25.00, 60);
        course4.addInstructor(instructor4);
        course5.addInstructor(instructor4);

        instructor5.addCourse(course2, 35.00, 60);
        instructor5.addCourse(course3, 37.00, 90);
        course2.addInstructor(instructor5);
        course3.addInstructor(instructor5);

        // Save updates to both sides
        instructorRepository.save(instructor1);
        instructorRepository.save(instructor2);
        instructorRepository.save(instructor3);
        instructorRepository.save(instructor4);
        instructorRepository.save(instructor5);

        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
        courseRepository.save(course4);
        courseRepository.save(course5);

        // Create and save students
        Student st1 = new Student("Maria", "maria@gmail.com", "1234", "student");
        Student st2 = new Student("Evi", "evi@gmail.com", "4321", "student");
        studentRepository.save(st1);
        studentRepository.save(st2);

    }

}
