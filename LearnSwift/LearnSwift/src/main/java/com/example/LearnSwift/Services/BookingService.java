package com.example.LearnSwift.Services;

import com.example.LearnSwift.Model.Booking;
import com.example.LearnSwift.Model.Course;
import com.example.LearnSwift.Model.Instructor;
import com.example.LearnSwift.Model.Student;
import com.example.LearnSwift.Repositories.BookingRepository;
import com.example.LearnSwift.Repositories.CourseRepository;
import com.example.LearnSwift.Repositories.InstructorRepository;
import com.example.LearnSwift.Repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service //This class is a service and has all the business logic. It communicates with repositories
public class BookingService {
    private final BookingRepository bookingRepository;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Autowired //injects the object into the constructor without having to create it manually
    public BookingService(BookingRepository bookingRepository, InstructorRepository instructorRepository, StudentRepository studentRepository, CourseRepository courseRepository) {

        this.bookingRepository = bookingRepository;
        this.instructorRepository = instructorRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        boolean exists = bookingRepository.existsById(id);
        if(!exists) {
            throw new IllegalArgumentException("Booking doesn't exists");
        }
        return bookingRepository.findById(id);

    }

    public void createBooking(Booking booking) {
        //check if the instructor exists
        Instructor instructor = instructorRepository.findById(booking.getInstructor().getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));
        //check if the course exists
        Course course = courseRepository.findById(booking.getCourse().getCourse_id())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        //check if student exists
        Student student = studentRepository.findById(booking.getStudent().getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        //Availability check
        List<Booking> existingBookings= instructor.getBookings();
        for (Booking existingBooking : existingBookings) {
            System.out.println("Existing Booking: " + existingBooking);
            //check if the hours overlap
            if ((booking.getStartTime().isBefore(existingBooking.getEndTime()) && booking.getEndTime().isAfter(existingBooking.getStartTime()))) {
                throw new IllegalArgumentException("Instructor is not available at this time.");}
        }

        //Check if the given duration match the duration of the course
        if(((Duration.between(booking.getStartTime(), booking.getEndTime()).toMinutes()))!= (long)(instructor.getCourseDuration(course.getCourse_id()))) {
            throw new IllegalArgumentException("The duration entered is incorrect. Please enter the correct duration according to the course.");
        }

        bookingRepository.save(booking);
    }

    public void deleteBooking(Long bookingId) {
        //check if the booking exists
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        bookingRepository.deleteById(bookingId);

    }

}
