package dev.api.user.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CompleteCourseResponse {
    private String courseId;
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
    private List<SectionResponse> sections;

    @Getter
    @Setter
    @Builder
    public static class SectionResponse {
        private String sectionId;
        private String title;
        private List<LessonResponse> lessons;
    }

    @Getter
    @Setter
    @Builder
    public static class LessonResponse {
        private String lessonId;
        private String title;
        private Integer durationMinutes;
        private Boolean isPreview;
        private String lessonPath;
        private List<ResourceResponse> resources;
    }

    @Getter
    @Setter
    @Builder
    public static class ResourceResponse {
        private String resourceId;
        private String title;
        private String resourcePath;
        private Boolean isPreview;
    }
}