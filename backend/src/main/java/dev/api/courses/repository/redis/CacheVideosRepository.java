package dev.api.courses.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.api.courses.model.redis.CacheVideos;

@Repository
public interface CacheVideosRepository extends CrudRepository<CacheVideos, String> {
}
