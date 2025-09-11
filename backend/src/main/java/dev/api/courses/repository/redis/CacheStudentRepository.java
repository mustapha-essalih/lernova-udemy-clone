package dev.api.courses.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.api.courses.model.redis.CacheStudent;


@Repository
public interface CacheStudentRepository extends CrudRepository<CacheStudent, String> {}
