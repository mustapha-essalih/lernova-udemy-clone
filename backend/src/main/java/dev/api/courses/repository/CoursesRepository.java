package dev.api.courses.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.api.courses.model.Courses;

@Repository
public interface CoursesRepository extends JpaRepository<Courses , Integer>{
    
}
