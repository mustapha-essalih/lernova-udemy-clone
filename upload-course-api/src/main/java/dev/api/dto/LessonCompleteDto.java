package dev.api.dto;

import dev.api.entity.LessonRedisEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class LessonCompleteDto {
    private String id;
    private String title;
    private String lessonType;
    private Integer durationMinutes;
    private Boolean isPreview;
    private String filePath;
    
    // Complete nested structure
    private List<ResourceCompleteDto> resources;
    
    // Factory method to create from LessonRedisEntity
    public static LessonCompleteDto fromEntity(LessonRedisEntity entity) {
        return LessonCompleteDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .lessonType(entity.getLessonType())
                .durationMinutes(entity.getDurationMinutes())
                .isPreview(entity.getIsPreview())
                .filePath(entity.getFilePath())
                .build();
    }
}