package dev.api.repository;

import dev.api.entity.CourseRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRedisRepository extends CrudRepository<CourseRedisEntity, String> {
}