package dev.api.courses.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.api.courses.model.redis.CacheSections;



@Repository
public interface CacheSectionsRepository extends CrudRepository<CacheSections , String> {
    
}
