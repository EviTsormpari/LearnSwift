package com.example.LearnSwift.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.*;

@Entity //This annotation specifies that the class is an entity and is mapped to a database table
public class Instructor extends User{

    //We need this list to use the many to many relationship.
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("instructor") // Avoid infinite recursion for bookings
    private final List<Course> courses = new ArrayList<>();

    //Map so each instructor can apply the price they want to the course
    @ElementCollection //This annotation is used to indicate that the field is a collection of elements (in this case, a map) that are not entities themselves but part of the owning entity
    @MapKeyJoinColumn(name = "course_id") // define the foreign key column in the collection table that will refer to the primary key
    private final Map<Long, Double> courses_price = new HashMap<>();

    //Map so each instructor can apply the duration they want to the course
    @ElementCollection
    @MapKeyJoinColumn(name = "course_id") // define the foreign key column in the collection table that will refer to the primary key
    private final Map<Long, Integer> courses_duration = new HashMap<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "review_id")
    @JsonManagedReference ("instructor_review")//to prevent infinite recursion
    private final List<Review> reviews = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "booking_id")
    @JsonManagedReference ("instructor_booking")//to prevent infinite recursion
    private final List<Booking> bookings = new ArrayList<>();


    public Instructor(String username, String email, String password, String role) {
        super(username, email, password, role);
    }

    public Instructor() {

    }

    public List<Course> getCourses() {
        return courses;
    }

    public List<Review> getReviews() {return reviews;}

    public List<Booking> getBookings() {
        return bookings;
    }

    public Map<Long, Double> getCourses_price() {
        return courses_price;
    }

    public Map<Long, Integer> getCourses_duration() {
        return courses_duration;
    }

    public double getCoursePrice(Long course_id) {
        for (Map.Entry<Long, Double> entry : courses_price.entrySet()) {
            Long courseId = entry.getKey();
             if(Objects.equals(courseId, course_id)) {
                return entry.getValue(); // Return the price
            }
        }
        throw new IllegalArgumentException("Course not found");
    }

    public int getCourseDuration(Long course_id) {
        for (Map.Entry<Long, Integer> entry : courses_duration.entrySet()) {
            Long courseId = entry.getKey();
            if(Objects.equals(courseId, course_id)) {
                return entry.getValue(); // Return the price
            }
        }
        throw new IllegalArgumentException("Course not found");
    }
    public void addBooking(Booking b) {bookings.add(b);}
    public void addCourse(Course c, Double price, int duration) {
       courses.add(c);
       courses_price.put(c.getCourse_id(), price);
       courses_duration.put(c.getCourse_id(),duration);
    }

    public void addReview(Review r) {
        reviews.add(r);
    }

    public void deleteCourse(Long id) {
        Course courseToRemove = null;

        for (Course course : courses) {
            if (course.getCourse_id().equals(id)) {
                courseToRemove = course;
                break;
            }
        }


        if (courseToRemove != null) {
            courses.remove(courseToRemove);
            courses_price.remove(courseToRemove);
        }
    }

    public void deleteReview(Long id) {
        reviews.removeIf(review -> review.getReview_id().equals(id));
    }

    public void removeBooking(Long bookingId) {
        bookings.removeIf(booking -> booking.getBooking_id().equals(bookingId));
    }

}
