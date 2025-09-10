package dev.api.instructors;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.api.authentication.model.BaseEntity;
import dev.api.common.ApiResponse;
import dev.api.courses.model.Courses;
import dev.api.courses.model.redis.CacheCourse;
import dev.api.courses.model.redis.CacheSections;
import dev.api.courses.repository.redis.CacheCourseRepository;
import dev.api.courses.repository.redis.CacheSectionsRepository;
import dev.api.courses.responses.CacheCourseResponse;
import dev.api.instructors.model.Instructors;
import dev.api.instructors.request.CourseInitRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

 

@AllArgsConstructor
@RequestMapping("/api/v1/instructor/courses")
@RestController
// @PreAuthorize("hasRole('INSTRUCTOR')")
public class InstructorCourseController {
    
    private InstructorCourseService instructorCourseService;
    private CacheSectionsRepository cacheSectionsRepository;

    private CacheCourseRepository cacheCourseRepository;

    
    @PostMapping("/init")
    public ResponseEntity<CacheCourseResponse> init(@RequestBody CourseInitRequest request) {
    
        CacheCourseResponse course = instructorCourseService.init(request);
        ApiResponse<CacheCourseResponse> response = new ApiResponse<>(true, "Course created successfully", course);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    
 
   @GetMapping
   public Iterable<CacheCourse> test(){
        return cacheCourseRepository.findAll();

   }


}
