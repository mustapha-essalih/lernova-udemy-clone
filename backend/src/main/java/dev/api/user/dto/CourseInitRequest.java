package dev.api.user.dto;

import java.math.BigDecimal;
import java.util.List;

import dev.api.common.enums.Languages;
import dev.api.common.enums.Level;
import dev.api.common.enums.Status;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class CourseInitRequest {

    @NotNull(message = "Title cannot be null")
    @NotEmpty(message = "Title cannot be empty")
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotNull(message = "Subtitle cannot be null")
    @NotEmpty(message = "Subtitle cannot be empty")
    @NotBlank(message = "Subtitle cannot be blank")
    private String subTitle;

    @NotNull(message = "Description cannot be null")
    @NotEmpty(message = "Description cannot be empty")
    @NotBlank(message = "Description cannot be blank")
    private String description;


    @NotNull(message = "Price cannot be null")
    @NotEmpty(message = "Price cannot be empty")
    @NotBlank(message = "Price cannot be blank")
    private String price;

    @NotNull(message = "isFree flag cannot be null")
    private Boolean isFree;

    @Min(value = 1, message = "Course duration must be at least 1 minute")
    private int courseDurationMinutes;

    @NotNull(message = "Language cannot be null")
    private Languages language;

    @NotNull(message = "Level cannot be null")
    private Level level;

    @NotNull
    private Integer subcategoryId;

    @NotNull(message = "Sections cannot be null")
    @NotEmpty(message = "A course must have at least one section")
    @Valid
    private List<SectionsInitRequest> sections;
}
