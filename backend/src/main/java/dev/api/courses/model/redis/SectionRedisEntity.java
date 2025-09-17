package dev.api.courses.model.redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


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
