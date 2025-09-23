package dev.api.courses.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.api.courses.model.redis.CourseTitleRedisEntity;

@Repository
public interface CourseTitleRedisRepository extends CrudRepository<CourseTitleRedisEntity, String> {}