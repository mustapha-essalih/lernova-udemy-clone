package dev.api.courses.model.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@RedisHash("resource")
public class ResourceRedisEntity {

    @Id
    private String id;
    private String title;
    private String resourcePath;
    private Boolean isPreview;
}
