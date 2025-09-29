package dev.api.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.api.common.ApiResponse;
import dev.api.courses.model.Course;
import dev.api.user.dto.CompleteCourseResponse;
import dev.api.user.dto.ReviewCourseRequest;
import dev.api.user.service.ManagerService;
import lombok.AllArgsConstructor;


@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/managers")
public class ManagerController {

    private ManagerService managerService;


    @GetMapping("/courses/pending")
    public ResponseEntity<ApiResponse<List<CompleteCourseResponse>>> getPendingCourses() {
    
        List<CompleteCourseResponse> pendingCourses = managerService.getPendingCourses();

        return ResponseEntity.ok().body(new ApiResponse<>(true, null, pendingCourses));
    }
 
    // Review a course (approve, reject, publish)
    @PutMapping("/courses/{courseId}/review")
    public ResponseEntity<ApiResponse<Object>> reviewCourse(@PathVariable String courseId, @RequestBody ReviewCourseRequest request) {
        managerService.reviewCourse(courseId, request);
        return ResponseEntity.ok().body(new ApiResponse<>(true, "Course reviewed successfully", null));
    }
}
