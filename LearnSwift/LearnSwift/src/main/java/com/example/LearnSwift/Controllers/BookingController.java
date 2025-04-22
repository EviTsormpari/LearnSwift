package com.example.LearnSwift.Controllers;

import com.example.LearnSwift.Model.Booking;
import com.example.LearnSwift.Services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController //this is a controller class
@RequestMapping("/api/bookings") //All the endpoints of this class start with this
public class BookingController {

    private final BookingService bookingService;

    @Autowired //injects the object into the constructor without having to create it manually
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/{booking_id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long booking_id) {
        Optional<Booking> booking = bookingService.getBookingById(booking_id);
        return booking.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<String> createBooking(@RequestBody Booking booking) {
        try {
            bookingService.createBooking(booking);
            return new ResponseEntity<>("Booking created successfully", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught exception: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);}
    }

    @DeleteMapping("/{booking_id}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long booking_id)  {
        System.out.println("Request received for DELETE booking with ID: " + booking_id);
        try {
            bookingService.deleteBooking(booking_id/*, student_id, instructor_id*/);
            return new ResponseEntity<>("Booking deleted successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Booking not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);}
    }
}
