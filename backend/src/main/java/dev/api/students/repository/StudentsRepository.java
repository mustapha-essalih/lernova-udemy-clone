package dev.api.students.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.api.students.model.Students;

@Repository
public interface StudentsRepository extends JpaRepository<Students, Integer> {
    Optional<Students> findByUsername(String username);

    Optional<Students> findByEmail(String email);

    Optional<Students> findByVerificationCode(String verificationCode);
}
