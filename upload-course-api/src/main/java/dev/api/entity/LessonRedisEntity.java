package dev.api.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@RedisHash("lessons")
public class LessonRedisEntity implements Serializable {

    @Id
    private String id;
    private String title;
    private Integer durationMinutes;
    private Boolean isPreview;
    private String lessonPath;
;

    private List<String> resourceIds = new ArrayList<>();
}