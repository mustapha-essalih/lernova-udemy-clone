package dev.api.courses.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dev.api.courses.model.Course;

@Repository
public interface CoursesRepository extends JpaRepository<Course , Integer>{
    
    @Query("SELECT c FROM Course c WHERE c.status = 'PENDING_REVIEW'")
    List<Course> findAllPendingCourses();
}
