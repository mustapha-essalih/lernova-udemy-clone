package dev.api.courses.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LessonResponseDto {

    private String lessonId;
    private List<ResourceResponseDto> resources;
}
