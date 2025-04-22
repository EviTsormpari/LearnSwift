package com.example.LearnSwift.Repositories;

import com.example.LearnSwift.Model.Student;
import com.example.LearnSwift.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository //It provides an interface for interacting with the database
public interface StudentRepository extends JpaRepository<Student, Long> {
    //custom method
    Optional<Student> findByEmail(String username);

    //custom method
    Optional<Student> findByUsername(String username);

    //custom method
    boolean existsByUsername(String username);

    //custom method
    boolean existsByEmail(String email);
}
