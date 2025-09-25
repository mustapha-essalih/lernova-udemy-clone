package dev.api.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import dev.api.common.exceptions.BadRequestException;
import dev.api.courses.model.Course;
import dev.api.courses.model.redis.CourseRedisEntity;
import dev.api.courses.model.redis.LessonRedisEntity;
import dev.api.courses.model.redis.ResourceRedisEntity;
import dev.api.courses.model.redis.SectionRedisEntity;
import dev.api.courses.repository.CoursesRepository;
import dev.api.courses.repository.redis.CourseRedisRepository;
import dev.api.courses.repository.redis.LessonRedisRepository;
import dev.api.courses.repository.redis.ResourcesRedisRepository;
import dev.api.courses.repository.redis.SectionsRedisRepository;
import dev.api.user.dto.CompleteCourseResponse;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@Service
public class ManagerService {

    private CoursesRepository coursesRepository;
    private CourseRedisRepository courseRedisRepository;
    private SectionsRedisRepository sectionsRedisRepository;
    private LessonRedisRepository lessonRedisRepository;
    private ResourcesRedisRepository resourcesRedisRepository;

    
    public List<CompleteCourseResponse> getPendingCourses() {
        List<CompleteCourseResponse> allCourses = new ArrayList<>();
        
        // Get all courses from Redis cache
        Iterable<CourseRedisEntity> cachedCourses = courseRedisRepository.findAll();
        
        for (CourseRedisEntity course : cachedCourses) {
            CompleteCourseResponse completeCourse = buildCompleteCourseResponse(course);
            allCourses.add(completeCourse);
        }
        
        return allCourses;
    }
 

    public CompleteCourseResponse buildCompleteCourseResponse(CourseRedisEntity course) {
      
        List<CompleteCourseResponse.SectionResponse> sections = new ArrayList<>();
        for (String sectionId : course.getSectionIds()) {
            SectionRedisEntity section = sectionsRedisRepository.findById(sectionId)
                .orElse(null);
            if (section != null) {
                List<CompleteCourseResponse.LessonResponse> lessons = new ArrayList<>();
                
                // Get all lessons for this section
                for (String lessonId : section.getLessonIds()) {
                    LessonRedisEntity lesson = lessonRedisRepository.findById(lessonId)
                        .orElse(null);
                    if (lesson != null) {
                        List<CompleteCourseResponse.ResourceResponse> resources = new ArrayList<>();
                        // Get all resources for this lesson

                        List<String> resourceIds = lesson.getResourceIds();
                        if (resourceIds != null) {
                            
                        
                        for (String resourceId : resourceIds) {
                            ResourceRedisEntity resource = resourcesRedisRepository.findById(resourceId)
                                .orElse(null);
                            if (resource != null) {
                                resources.add(CompleteCourseResponse.ResourceResponse.builder()
                                    .resourceId(resource.getId())
                                    .title(resource.getTitle())
                                    .resourcePath(resource.getResourcePath())
                                    .isPreview(resource.getIsPreview())
                                    .build());
                            }
                        }
                    }
                        lessons.add(CompleteCourseResponse.LessonResponse.builder()
                            .lessonId(lesson.getId())
                            .title(lesson.getTitle())
                            .durationMinutes(lesson.getDurationMinutes())
                            .isPreview(lesson.getIsPreview())
                            .lessonPath(lesson.getLessonPath())
                            .resources(resources)
                            .build());
                    }
                }
                
                sections.add(CompleteCourseResponse.SectionResponse.builder()
                    .sectionId(section.getId())
                    .title(section.getTitle())
                    .lessons(lessons)
                    .build());
            }
        }
        
        return CompleteCourseResponse.builder()
            .courseId(course.getId())
            .instructorId(course.getInstructorId())
            .title(course.getTitle())
            .subTitle(course.getSubTitle())
            .description(course.getDescription())
            .price(course.getPrice())
            .isFree(course.getIsFree())
            .language(course.getLanguage())
            .couponCode(course.getCouponCode())
            .courseDurationMinutes(course.getCourseDurationMinutes())
            .status(course.getStatus())
            .level(course.getLevel())
            .subcategoryId(course.getSubcategoryId())
            .imagePath(course.getImagePath())
            .sections(sections)
            .build();
    }
 

    public Object getInstructorsWithCourses() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getInstructorsWithCourses'");
    }

    public Object previewCourse(Long courseId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'previewCourse'");
    }



}
