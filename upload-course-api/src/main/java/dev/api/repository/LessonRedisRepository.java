package dev.api.repository;

import dev.api.entity.LessonRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRedisRepository extends CrudRepository<LessonRedisEntity, String> {
}