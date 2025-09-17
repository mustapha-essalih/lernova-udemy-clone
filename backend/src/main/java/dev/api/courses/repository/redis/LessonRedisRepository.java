package dev.api.courses.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.api.courses.model.redis.LessonRedisEntity;

@Repository
public interface LessonRedisRepository extends CrudRepository<LessonRedisEntity , String>{
    
}
