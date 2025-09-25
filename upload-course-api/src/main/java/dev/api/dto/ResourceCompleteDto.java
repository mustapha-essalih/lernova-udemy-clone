package dev.api.dto;

import dev.api.entity.ResourceRedisEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResourceCompleteDto {
    private String id;
    private String title;
    private String resourceUrl;
    private Boolean isPreview;
    private String filePath;
    
    // Factory method to create from ResourceRedisEntity
    public static ResourceCompleteDto fromEntity(ResourceRedisEntity entity) {
        return ResourceCompleteDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .resourceUrl(entity.getResourceUrl())
                .isPreview(entity.getIsPreview())
                .filePath(entity.getFilePath())
                .build();
    }
}