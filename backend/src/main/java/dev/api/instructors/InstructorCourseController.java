package dev.api.instructors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.api.common.ApiResponse;
import dev.api.courses.model.Courses;
import dev.api.instructors.model.Instructors;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
 

@AllArgsConstructor
@RequestMapping("/api/instructor/courses")
@RestController
// @PreAuthorize("hasRole('INSTRUCTOR')")
public class InstructorCourseController {
    
    private InstructorCourseService instructorCourseService;
    




    @PostMapping
    public ResponseEntity<ApiResponse<Courses>> createCourse(@AuthenticationPrincipal Integer instructorId) {
        
        Courses course = instructorCourseService.createCourse(instructorId);
        ApiResponse<Courses> response = new ApiResponse<>(true, "Course created successfully", course);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

 


}
