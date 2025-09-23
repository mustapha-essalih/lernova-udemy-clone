package dev.api.courses.model.redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@RedisHash("lessons")
public class LessonRedisEntity implements Serializable {

    @Id
    private String id;
    private String title;
    private String lessonType;
    private Integer durationMinutes;
    private Boolean isPreview;
    private String filePath;

    private List<String> resourceIds = new ArrayList<>();
}
