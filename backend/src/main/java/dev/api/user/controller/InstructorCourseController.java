package dev.api.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.api.common.ApiResponse;
import dev.api.common.BaseEntity;
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
            @RequestParam("image") MultipartFile imageFile,
            @AuthenticationPrincipal BaseEntity user) {
        
        String imagePath = instructorCourseService.storeCourseImage(courseId, imageFile, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Course image uploaded successfully", imagePath));
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal BaseEntity user) {
        
        String filePath = instructorCourseService.uploadFileToTmp(file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "File uploaded successfully", filePath));
    }

}
