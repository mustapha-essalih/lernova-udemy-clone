package dev.api.courses.model.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@RedisHash("course_titles")
public class CourseTitleRedisEntity {
    
    @Id
    private String id;
    
    private String title;
}