package dev.api.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@RedisHash("courses")
@Getter
@Setter
@Builder
public class CourseRedisEntity implements Serializable {

    @Id
    private String id;
    private Integer instructorId;
    private String title;
    private String subTitle;
    private String description;
    private String price;
    private Boolean isFree;
    private String language;
    private String couponCode;
    private Integer courseDurationMinutes;
    private String status;
    private String level;
    private Integer subcategoryId;
    private String imagePath;

    private List<String> sectionIds = new ArrayList<>();
}