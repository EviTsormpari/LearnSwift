package com.example.LearnSwift.Repositories;

import com.example.LearnSwift.Model.Booking;
import com.example.LearnSwift.Model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository //It provides an interface for interacting with the database
public interface BookingRepository extends JpaRepository<Booking, Long> {

    //custom method
    List<Booking> findByInstructorAndBookingDate(Instructor instructor, Date bookingDate);
}
