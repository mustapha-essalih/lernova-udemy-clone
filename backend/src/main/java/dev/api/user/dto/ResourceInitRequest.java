package dev.api.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResourceInitRequest {

    
    @NotNull(message = "resource title cannot be null")
    @NotEmpty(message = "resource title cannot be empty")
    @NotBlank(message = "resource title cannot be blank")
    private String title;

    private String resourceUrl;

    @NotNull
    private Boolean isPreview;

}
