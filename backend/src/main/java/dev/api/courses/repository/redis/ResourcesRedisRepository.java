package dev.api.courses.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.api.courses.model.redis.ResourceRedisEntity;
 



@Repository
public interface ResourcesRedisRepository extends CrudRepository<ResourceRedisEntity, String> {
}