package dev.api.user.service;
 
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import dev.api.common.exceptions.BadRequestException;
import dev.api.courses.dto.CourseInitResponseDto;
import dev.api.courses.dto.LessonResponseDto;
import dev.api.courses.dto.ResourceResponseDto;
import dev.api.courses.dto.SectionResponseDto;
import dev.api.courses.model.redis.CourseRedisEntity;
import dev.api.courses.model.redis.LessonRedisEntity;
import dev.api.courses.model.redis.ResourceRedisEntity;
import dev.api.courses.model.redis.SectionRedisEntity;
import dev.api.courses.repository.CoursesRepository;
import dev.api.courses.repository.redis.CourseRedisRepository;
import dev.api.courses.repository.redis.LessonRedisRepository;
import dev.api.courses.repository.redis.ResourcesRedisRepository;
import dev.api.courses.repository.redis.SectionsRedisRepository;
import dev.api.user.dto.CourseInitRequest;
import dev.api.user.dto.LessonInitRequest;
import dev.api.user.dto.ResourceInitRequest;
import dev.api.user.dto.SectionsInitRequest;
import dev.api.user.repository.InstructorsRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class InstructorCourseService {


    private CourseRedisRepository courseRedisRepository;
    private SectionsRedisRepository sectionsRedisRepository;
    private ResourcesRedisRepository resourcesRedisRepository;
    private CoursesRepository coursesRepository;
    private InstructorsRepository instructorsRepository;
    private LessonRedisRepository lessonRedisRepository;


    
     

     public CourseInitResponseDto init(CourseInitRequest request, Integer instructorId) {
        

        // check if course alredy exist
        // allCourses.forEach((course) -> course.equals(allCourses));
 
        isValidPriceScale(request.getPrice());

        List<SectionResponseDto> sectionResponses = new ArrayList<>();
        List<String> sectionIds = new ArrayList<>();
        
        List<ResourceRedisEntity> allResourceEntities = new ArrayList<>();
        List<LessonRedisEntity> allLessonEntities = new ArrayList<>();
        List<SectionRedisEntity> allSectionEntities = new ArrayList<>();

        List<SectionsInitRequest> sectionsRequest = request.getSections();

        for (SectionsInitRequest sectionDto : sectionsRequest) {

            List<LessonResponseDto> lessonResponses = new ArrayList<>();
            List<String> lessonIds = new ArrayList<>();

            List<LessonInitRequest> lessonsRequest = sectionDto.getLessons();

            for (LessonInitRequest lessonDto : lessonsRequest) {

                List<ResourceResponseDto> resourceResponses = new ArrayList<>();
                List<String> resourceIds = new ArrayList<>();

                List<ResourceInitRequest> resourcesRequest = lessonDto.getResources();

                for (ResourceInitRequest resourceDto : resourcesRequest) {

                    String resourceId = "resource_" + UUID.randomUUID().toString();
                    resourceIds.add(resourceId);
                    resourceResponses.add(new ResourceResponseDto(resourceId));

                    ResourceRedisEntity resourceEntity = ResourceRedisEntity.builder()
                            .id(resourceId)
                            .title(resourceDto.getTitle())
                            .isPreview(resourceDto.getIsPreview())
                            .fileType(resourceDto.getFileType())
                            .build();
                    allResourceEntities.add(resourceEntity);
                }

                String lessonId = "lesson_" + UUID.randomUUID().toString();
                lessonIds.add(lessonId);
                lessonResponses.add(new LessonResponseDto(lessonId, resourceResponses));

                LessonRedisEntity lessonEntity = LessonRedisEntity.builder()
                        .id(lessonId)
                        .title(lessonDto.getTitle())
                        .lessonType(lessonDto.getLessonType().name())
                        .durationMinutes(lessonDto.getDurationMinutes())
                        .isPreview(lessonDto.getIsPreview())
                        .resourceIds(resourceIds)
                        .build();
                allLessonEntities.add(lessonEntity);
            }

            String sectionId = "section_" + UUID.randomUUID().toString();
            sectionIds.add(sectionId);
            sectionResponses.add(new SectionResponseDto(sectionId, lessonResponses));

            SectionRedisEntity sectionEntity = SectionRedisEntity.builder()
                    .id(sectionId)
                    .title(sectionDto.getTitle())
                    .lessonIds(lessonIds)
                    .build();
            allSectionEntities.add(sectionEntity);
        }
        
        resourcesRedisRepository.saveAll(allResourceEntities);
        lessonRedisRepository.saveAll(allLessonEntities);
        sectionsRedisRepository.saveAll(allSectionEntities);

        String courseId = "course_" + UUID.randomUUID().toString();

        CourseRedisEntity courseRedisEntity = CourseRedisEntity.builder()
                .id(courseId)
                .instructorId(instructorId)
                .title(request.getTitle())
                .subTitle(request.getSubTitle())
                .description(request.getDescription())
                .price(request.getPrice().toString())
                .isFree(request.getIsFree())
                .language(request.getLanguage().name())
                .courseDurationMinutes(request.getCourseDurationMinutes())
                .level(request.getLevel().name())
                .subcategoryId(request.getSubcategoryId())
                .sectionIds(sectionIds)
                .build();
        
        courseRedisRepository.save(courseRedisEntity);

        return new CourseInitResponseDto(courseId, sectionResponses);
    }


    private void isValidPriceScale(String priceString) {
        try {
            BigDecimal price = new BigDecimal(priceString);
            if (price.scale() != 2){
                throw new BadRequestException("invalid price");
            }
        } catch (NumberFormatException | NullPointerException e) {
            throw new BadRequestException("invalid price");
        }
    }
}
