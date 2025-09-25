package dev.api.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@Builder
@RedisHash("resource")
public class ResourceRedisEntity implements Serializable {

    @Id
    private String id;
    private String title;
    private String resourcePath;
    private Boolean isPreview;
}