package dev.api.students.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.api.students.model.Student;

@Repository
public interface StudentsRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByUsername(String username);

    Optional<Student> findByEmail(String email);

    Optional<Student> findByVerificationCode(String verificationCode);
}
