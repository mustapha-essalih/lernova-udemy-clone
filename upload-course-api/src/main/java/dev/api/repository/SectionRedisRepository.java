package dev.api.repository;

import dev.api.entity.SectionRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRedisRepository extends CrudRepository<SectionRedisEntity, String> {
}