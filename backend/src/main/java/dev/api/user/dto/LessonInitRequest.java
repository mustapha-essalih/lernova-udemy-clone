package dev.api.user.dto;

import java.util.List;

import dev.api.common.enums.LessonType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LessonInitRequest {

    @NotNull(message = "Lesson title cannot be null")
    @NotEmpty(message = "Lesson title cannot be empty")
    @NotBlank(message = "Lesson title cannot be blank")
    private String title;

    @NotNull(message = "Lesson type cannot be null")
    private LessonType lessonType;

    private Integer durationMinutes;
    private Boolean isPreview;
    
    @Valid
    private List<ResourceInitRequest> resources;
}
