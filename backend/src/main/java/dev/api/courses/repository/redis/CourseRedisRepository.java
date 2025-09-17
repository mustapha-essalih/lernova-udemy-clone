package dev.api.courses.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.api.courses.model.redis.CourseRedisEntity;

 
@Repository
public interface CourseRedisRepository extends CrudRepository<CourseRedisEntity , String>{

    boolean existsByTitle(String title);

    Boolean findByTitle(String title);
    
}
