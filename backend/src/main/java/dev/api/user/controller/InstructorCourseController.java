package dev.api.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.api.common.ApiResponse;
import dev.api.common.BaseEntity;
import dev.api.user.dto.CompleteCourseResponse;
import dev.api.user.dto.CourseInitRequest;
import dev.api.user.service.InstructorCourseService;
import lombok.AllArgsConstructor;



@PreAuthorize("hasRole('INSTRUCTOR')")
@AllArgsConstructor
@RequestMapping("/api/v1/instructor/courses")
@RestController
public class InstructorCourseController {
    
    private InstructorCourseService instructorCourseService;
    
    
    @PostMapping("/metadata")
    public ResponseEntity<ApiResponse<String>> storeCourseMetadata(@RequestBody CourseInitRequest request , @AuthenticationPrincipal BaseEntity user) {
        
        String courseId = instructorCourseService.storeCourseMetadata(request , user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Course created successfully", courseId));
    }

    @PostMapping("/image")
    public ResponseEntity<ApiResponse<String>> uploadCourseImage(
            @RequestParam String courseId,
            @RequestParam MultipartFile image,
            @AuthenticationPrincipal BaseEntity user) {
        
        String imagePath = instructorCourseService.storeCourseImage(courseId, image, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Single course image uploaded successfully", imagePath));
    }


    // TODO:
    // get course from postgres
    // update course
    // delete course
    
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CompleteCourseResponse>> getCourseFromCache(
            @PathVariable String courseId , @AuthenticationPrincipal BaseEntity user) {
        
        CompleteCourseResponse course = instructorCourseService.getCourseFromCache(courseId , user.getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Course retrieved successfully", course));
    }


}
