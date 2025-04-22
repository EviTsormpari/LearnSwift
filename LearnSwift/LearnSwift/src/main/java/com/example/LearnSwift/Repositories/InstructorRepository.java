package com.example.LearnSwift.Repositories;

import com.example.LearnSwift.Model.Instructor;
import com.example.LearnSwift.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository //It provides an interface for interacting with the database
public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    //custom method
    Optional<Instructor> findByEmail(String username);
    //custom method
    boolean existsByUsername(String username);
    //custom method
    Optional<Instructor> findByUsername(String username);

    boolean existsByEmail(String email);
}
