package dev.api.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
public class ResourceInitRequest {

    @NotNull(message = "Resource title cannot be null")
    @NotEmpty(message = "Resource title cannot be empty")
    @NotBlank(message = "Resource title cannot be blank")
    private String title;

    @NotNull(message = "is Preview cannot be null")
    private Boolean isPreview;

    @NotNull(message = "File type cannot be null")
    @NotEmpty(message = "File type cannot be empty")
    @NotBlank(message = "File type cannot be blank")
    private String fileType;
}
