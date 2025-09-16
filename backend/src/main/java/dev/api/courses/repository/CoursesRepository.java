package dev.api.courses.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dev.api.courses.model.Courses;

@Repository
public interface CoursesRepository extends JpaRepository<Courses , Integer>{
    
    @Query("SELECT c FROM Courses c WHERE c.status = 'PENDING_REVIEW'")
    List<Courses> findAllPendingCourses();
}
