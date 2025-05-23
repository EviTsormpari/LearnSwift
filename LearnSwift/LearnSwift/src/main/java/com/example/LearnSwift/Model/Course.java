package com.example.LearnSwift.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity //This annotation specifies that the class is an entity and is mapped to a database table
public class Course {
    @Id //the primary key of the entity
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //automatically generated by the database
    private Long course_id;
    private String title;
    //the relationships between courses and instructors. With the cascade we ensure that any course change will also apply to instructors
    @ManyToMany(mappedBy = "courses",fetch = FetchType.LAZY)
    @JsonIgnore //to prevent infinite recursion
    private final List<Instructor> instructors = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "booking_id")
    @JsonManagedReference("course_booking")
    private final List<Booking> bookings = new ArrayList<>();

    public Course(String title) {
        this.title = title;
    }

    public Course() {
    }

    //GETTERS
    public Long getCourse_id() {
        return course_id;
    }

    public String getTitle() {
        return title;
    }

    public List<Instructor> getInstructors() {
        return instructors;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    //SETTERS
    public void setCourse_id(Long courseΙd) {
        this.course_id = courseΙd;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addInstructor(Instructor i) {
        instructors.add(i);
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    @Override
    public String toString() {
        return "Course Title: " + title;
    }
}
