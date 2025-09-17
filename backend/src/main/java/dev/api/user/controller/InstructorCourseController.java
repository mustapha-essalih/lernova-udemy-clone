package dev.api.user.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.api.common.ApiResponse;
import dev.api.common.BaseEntity;
import dev.api.courses.dto.CourseInitResponseDto;
import dev.api.user.dto.CourseInitRequest;
import dev.api.user.service.InstructorCourseService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@PreAuthorize("hasRole('INSTRUCTOR')")
@AllArgsConstructor
@RequestMapping("/api/v1/instructor/courses")
@RestController
public class InstructorCourseController {
    
    private InstructorCourseService instructorCourseService;
    
    
    @PostMapping("/init")
    public ResponseEntity<ApiResponse<CourseInitResponseDto>> init(@RequestBody CourseInitRequest request , @AuthenticationPrincipal BaseEntity user) {
        
        CourseInitResponseDto courseMetaData = instructorCourseService.init(request , user.getId());
        ApiResponse<CourseInitResponseDto> response = new ApiResponse<>(true, "Course created successfully", courseMetaData);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    


    

}
