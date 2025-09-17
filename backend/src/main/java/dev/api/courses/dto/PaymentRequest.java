package dev.api.courses.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PaymentRequest {

    @NotNull(message = "course id is required")
    private Integer courseId;
    
    @NotEmpty(message = "course name is required")
    @NotNull(message = "course name is required")
    @NotBlank(message = "course name is required")
    private String courseName;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Long amount; // amount in cents (e.g., $10.99 = 1099)

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }
    
    
    
}
