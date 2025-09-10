package dev.api.courses.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.api.courses.model.redis.CacheCourse;

@Repository
public interface CacheCourseRepository extends CrudRepository<CacheCourse , String>{
    
}
