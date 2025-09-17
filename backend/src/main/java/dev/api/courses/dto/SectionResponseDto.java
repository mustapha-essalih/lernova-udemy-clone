package dev.api.courses.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SectionResponseDto {

    private String sectionId;
    private List<LessonResponseDto> lessons;
}
