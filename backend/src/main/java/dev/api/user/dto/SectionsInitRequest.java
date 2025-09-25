package dev.api.user.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

 

@Getter
@Setter
public class SectionsInitRequest {

    @NotNull(message = "Section title cannot be null")
    @NotEmpty(message = "Section title cannot be empty")
    @NotBlank(message = "Section title cannot be blank")
    private String title;

    @NotNull(message = "Lessons cannot be null")
    @NotEmpty(message = "A section must have at least one lesson")
    @Valid
    private List<LessonInitRequest> lessons;
}
