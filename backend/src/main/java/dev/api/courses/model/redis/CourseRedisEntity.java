package dev.api.courses.model.redis;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import dev.api.common.enums.Languages;
import dev.api.common.enums.Level;
import dev.api.common.enums.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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

    private List<String> sectionIds = new ArrayList<>();
}
