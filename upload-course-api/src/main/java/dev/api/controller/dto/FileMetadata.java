package dev.api.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileMetadata {

    @NotNull(message = "Course title cannot be null")
    @NotEmpty(message = "Course title cannot be empty")
    @NotBlank(message = "Course title cannot be blank")
    private String courseTitile;
    
    @NotNull(message = "Section title cannot be null")
    @NotEmpty(message = "Section title cannot be empty")
    @NotBlank(message = "Section title cannot be blank")
    private String sectionTitile;
    

    @NotNull(message = "lesson title cannot be null")
    @NotEmpty(message = "lesson title cannot be empty")
    @NotBlank(message = "lesson title cannot be blank")
    private String lessonTitle;

    private String resourceTitle;

}
