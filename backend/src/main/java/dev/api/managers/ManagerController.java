package dev.api.managers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.api.common.ApiResponse;
import dev.api.courses.model.Course;
import lombok.AllArgsConstructor;


// @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/managers")
public class ManagerController {

    private ManagerService managerService;


    @GetMapping("/courses/pending")
    public List<Course> getPendingCourses() {
    
        return managerService.getPendingCourses();
    }
 
    
     @GetMapping("/instructors")
    public ResponseEntity<?> getInstructorsWithCourses() {
        return ResponseEntity.ok(managerService.getInstructorsWithCourses());
    }

    // Preview a specific course
    @GetMapping("/courses/{courseId}/preview")
    public ResponseEntity<?> previewCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(managerService.previewCourse(courseId));
    }

    // Review a course (approve, reject, publish)
    @PostMapping("/courses/{courseId}/review")
    public ResponseEntity<Void> reviewCourse(@PathVariable Long courseId) {

    //    managerService.reviewCourse(courseId);
        return ResponseEntity.ok().build();
    }
}
