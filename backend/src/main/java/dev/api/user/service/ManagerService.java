package dev.api.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import dev.api.common.exceptions.ResourceNotFoundException;
import dev.api.courses.model.Course;
import dev.api.courses.model.Section;
import dev.api.courses.model.Lesson;
import dev.api.courses.model.Resource;
import dev.api.courses.model.VideoContent;
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
import dev.api.user.dto.ReviewCourseRequest;
import dev.api.user.repository.InstructorsRepository;
import dev.api.common.enums.Languages;
import dev.api.common.enums.Level;
import dev.api.common.enums.Status;
import dev.api.common.enums.LessonType;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@Service
public class ManagerService {

    private CoursesRepository coursesRepository;
    private CourseRedisRepository courseRedisRepository;
    private SectionsRedisRepository sectionsRedisRepository;
    private LessonRedisRepository lessonRedisRepository;
    private ResourcesRedisRepository resourcesRedisRepository;
    private InstructorsRepository instructorsRepository;

    public List<CompleteCourseResponse> getPendingCourses() {
        List<CompleteCourseResponse> allCourses = new ArrayList<>();
        
        
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
                
                
                for (String lessonId : section.getLessonIds()) {
                    LessonRedisEntity lesson = lessonRedisRepository.findById(lessonId)
                        .orElse(null);
                    if (lesson != null) {
                        List<CompleteCourseResponse.ResourceResponse> resources = new ArrayList<>();
                        

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
 



    public void reviewCourse(String courseId, ReviewCourseRequest request) {

        CourseRedisEntity redisCourse = courseRedisRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        CompleteCourseResponse completeCourseResponse = buildCompleteCourseResponse(redisCourse);
        
        
        Course course = mapCompleteCourseResponseToCourse(completeCourseResponse);
        
        if (request.getApproved()) {

            // use ai for elasticsearch
            course.setStatus(Status.PUBLISHED);
        }else{
            course.setStatus(Status.REJECTED);
        }
        
        coursesRepository.save(course);
    }


    private Course mapCompleteCourseResponseToCourse(CompleteCourseResponse completeCourseResponse) {
        Course course = Course.builder()
            .title(completeCourseResponse.getTitle())
            .subTitle(completeCourseResponse.getSubTitle())
            .description(completeCourseResponse.getDescription())
            .price(new BigDecimal(completeCourseResponse.getPrice()))
            .isFree(completeCourseResponse.getIsFree())
            .language(Languages.valueOf(completeCourseResponse.getLanguage()))
            .couponCode(completeCourseResponse.getCouponCode())
            .courseDurationMinutes(completeCourseResponse.getCourseDurationMinutes())
            .level(Level.valueOf(completeCourseResponse.getLevel()))
            .rating(new BigDecimal("0.0"))
            .sections(new HashSet<>())
            .build();

        
        Integer instructorId = completeCourseResponse.getInstructorId();
        if (instructorId != null) {
            instructorsRepository.findById(instructorId).ifPresent(course::setInstructor);
        }

        
        if (completeCourseResponse.getSections() != null) {
            Set<Section> sections = new HashSet<>();
            
            for (CompleteCourseResponse.SectionResponse sectionResponse : completeCourseResponse.getSections()) {
                Section section = Section.builder()
                    .title(sectionResponse.getTitle())
                    .course(course)
                    .lessons(new HashSet<>())
                    .build();
                
                if (sectionResponse.getLessons() != null) {
                    Set<Lesson> lessons = new HashSet<>();
                    
                    for (CompleteCourseResponse.LessonResponse lessonResponse : sectionResponse.getLessons()) {
                        Lesson lesson = Lesson.builder()
                            .title(lessonResponse.getTitle())
                            .section(section)
                            .resources(new HashSet<>())
                            .build();
                        
                        
                        if (lessonResponse.getDurationMinutes() == null) {
                            
                            lesson.setTextUrl(lessonResponse.getLessonPath()); 
                            lesson.setLessonType(LessonType.TEXT);
                        } else {
                            
                            VideoContent videoContent = VideoContent.builder()
                                .videoUrl(lessonResponse.getLessonPath()) 
                                .durationMinutes(lessonResponse.getDurationMinutes())
                                .isPreview(lessonResponse.getIsPreview())
                                .lesson(lesson)
                                .build();
                            lesson.setVideoContent(videoContent);
                            lesson.setLessonType(LessonType.VIDEO);
                        }
                        
                        if (lessonResponse.getResources() != null) {
                            Set<Resource> resources = new HashSet<>();
                            
                            for (CompleteCourseResponse.ResourceResponse resourceResponse : lessonResponse.getResources()) {
                                Resource resource = Resource.builder()
                                    .title(resourceResponse.getTitle())
                                    .resourceUrl(resourceResponse.getResourcePath()) 
                                    .isPreview(resourceResponse.getIsPreview())
                                    .lesson(lesson)
                                    .build();
                                
                                resources.add(resource);
                            }
                            
                            lesson.setResources(resources);
                        }
                        
                        lessons.add(lesson);
                    }
                    
                    section.setLessons(lessons);
                }
                
                sections.add(section);
            }
            
            course.setSections(sections);
        }
        
        return course;
    }

}
