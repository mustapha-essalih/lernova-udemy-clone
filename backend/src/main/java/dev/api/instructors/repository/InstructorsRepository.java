package dev.api.instructors.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.api.instructors.model.Instructors;
 
@Repository
public interface InstructorsRepository extends JpaRepository<Instructors, Integer>{
    Optional<Instructors> findByUsername(String username);

    Optional<Instructors> findByEmail(String email);

    Optional<Instructors> findByVerificationCode(String verificationCode);

}
