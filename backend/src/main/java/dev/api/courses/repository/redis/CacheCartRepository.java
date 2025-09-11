package dev.api.courses.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.api.courses.model.redis.CacheCart;

@Repository
public interface CacheCartRepository extends CrudRepository<CacheCart, String> {}