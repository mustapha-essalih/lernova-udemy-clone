package dev.api.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@RedisHash("sections")
public class SectionRedisEntity implements Serializable {

    @Id
    private String id;
    private String title;
    
    private List<String> lessonIds = new ArrayList<>();
}