package dev.api.courses.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CourseInitResponseDto {

    private String courseId;
    private List<SectionResponseDto> sections;
}
