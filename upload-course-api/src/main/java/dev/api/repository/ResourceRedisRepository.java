package dev.api.repository;

import dev.api.entity.ResourceRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRedisRepository extends CrudRepository<ResourceRedisEntity, String> {
}