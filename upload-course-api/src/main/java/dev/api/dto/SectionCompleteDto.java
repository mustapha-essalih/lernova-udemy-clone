package dev.api.dto;

import dev.api.entity.SectionRedisEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SectionCompleteDto {
    private String id;
    private String title;
    
    // Complete nested structure
    private List<LessonCompleteDto> lessons;
    
    // Factory method to create from SectionRedisEntity
    public static SectionCompleteDto fromEntity(SectionRedisEntity entity) {
        return SectionCompleteDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .build();
    }
}