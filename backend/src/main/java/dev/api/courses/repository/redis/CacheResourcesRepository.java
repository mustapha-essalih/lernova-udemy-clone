package dev.api.courses.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.api.courses.model.redis.CacheResources;

@Repository
public interface CacheResourcesRepository extends CrudRepository<CacheResources, String> {
}