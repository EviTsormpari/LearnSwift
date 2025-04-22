package com.example.LearnSwift.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity //This annotation specifies that the class is an entity and is mapped to a database table
public class Student extends User{

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "student_id") // define the foreign key column in the collection table that will refer to the primary key
    @JsonManagedReference("student_booking")//to prevent infinite recursion
    private List<Booking> booking_history;
    public Student(String username, String email, String password, String role) {
        super(username, email, password, role);
        this.booking_history = new ArrayList<>(); // Ensure the list is initialized
    }

    public Student() {
    }

    public void setBooking_history(List<Booking> booking_history) {
        this.booking_history = booking_history;
    }

    public List<Booking> getBooking_history() {
        return booking_history;
    }

    public void addBooking(Booking b) {
        booking_history.add(b);
    }

    public void removeBooking(Long bookingId) {
        booking_history.removeIf(booking -> booking.getBooking_id().equals(bookingId));
    }
}
