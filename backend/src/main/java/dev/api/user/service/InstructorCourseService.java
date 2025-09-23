package dev.api.user.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dev.api.common.exceptions.BadRequestException;
import dev.api.common.exceptions.InternalServerError;
import dev.api.courses.model.redis.CourseRedisEntity;
import dev.api.courses.model.redis.CourseTitleRedisEntity;
import dev.api.courses.model.redis.LessonRedisEntity;
import dev.api.courses.model.redis.ResourceRedisEntity;
import dev.api.courses.model.redis.SectionRedisEntity;
import dev.api.courses.repository.CoursesRepository;
import dev.api.courses.repository.redis.CourseRedisRepository;
import dev.api.courses.repository.redis.CourseTitleRedisRepository;
import dev.api.courses.repository.redis.LessonRedisRepository;
import dev.api.courses.repository.redis.ResourcesRedisRepository;
import dev.api.courses.repository.redis.SectionsRedisRepository;
import dev.api.user.dto.CourseInitRequest;
import dev.api.user.dto.LessonInitRequest;
import dev.api.user.dto.ResourceInitRequest;
import dev.api.user.dto.SectionsInitRequest;
import dev.api.user.repository.InstructorsRepository;

@Service
public class InstructorCourseService {

    @Value("${app.upload-directory}")
    private String uploadPath;

    private CourseRedisRepository courseRedisRepository;
    private SectionsRedisRepository sectionsRedisRepository;
    private ResourcesRedisRepository resourcesRedisRepository;
    private CoursesRepository coursesRepository;
    private InstructorsRepository instructorsRepository;
    private LessonRedisRepository lessonRedisRepository;
    private CourseTitleRedisRepository courseTitleRedisRepository;

    public InstructorCourseService(CourseRedisRepository courseRedisRepository,
            SectionsRedisRepository sectionsRedisRepository, ResourcesRedisRepository resourcesRedisRepository,
            CoursesRepository coursesRepository, InstructorsRepository instructorsRepository,
            LessonRedisRepository lessonRedisRepository, CourseTitleRedisRepository courseTitleRedisRepository) {
        this.courseRedisRepository = courseRedisRepository;
        this.sectionsRedisRepository = sectionsRedisRepository;
        this.resourcesRedisRepository = resourcesRedisRepository;
        this.coursesRepository = coursesRepository;
        this.instructorsRepository = instructorsRepository;
        this.lessonRedisRepository = lessonRedisRepository;
        this.courseTitleRedisRepository = courseTitleRedisRepository;
    }

    public String storeCourseMetadata(CourseInitRequest request, Integer instructorId) {

        validateAndSaveCourseTitle(request.getTitle());

        isValidPriceScale(request.getPrice());
        
        String courseId = "course_" + UUID.randomUUID().toString();
        Path courseDir = createCourseDirectory(request.getTitle());
        
        CourseEntitiesHolder entitiesHolder = new CourseEntitiesHolder();
        List<String> sectionIds = processSections(request.getSections(), courseDir, entitiesHolder);
        
        saveAllEntities(entitiesHolder);
        saveCourseEntity(request, instructorId, courseId, sectionIds);
        
        return courseId;
    }

    private void validateAndSaveCourseTitle(String title) {
        Iterable<CourseTitleRedisEntity> allCourseTiles = courseTitleRedisRepository.findAll();
        for (CourseTitleRedisEntity courseTitle : allCourseTiles) {
            if (courseTitle.getTitle().equals(title)) {
                throw new BadRequestException("Course title already exists");
            }
        }
        
        CourseTitleRedisEntity courseTitleRedisEntity = CourseTitleRedisEntity.builder()
                .id("title_" + UUID.randomUUID().toString())
                .title(title)
                .build();
        courseTitleRedisRepository.save(courseTitleRedisEntity);
    }

    private Path createCourseDirectory(String title) {
        Path courseDir = Paths.get(uploadPath, sanitizeTitle(title));
        try {
            Files.createDirectories(courseDir);
        } catch (IOException e) {
            throw new InternalServerError("Failed to create course directory");
        }
        return courseDir;
    }

    private List<String> processSections(List<SectionsInitRequest> sectionsRequest, Path courseDir, CourseEntitiesHolder entitiesHolder) {
        List<String> sectionIds = new ArrayList<>();
        
        for (SectionsInitRequest sectionDto : sectionsRequest) {
            String sectionId = "section_" + UUID.randomUUID().toString();
            Path sectionDir = createSectionDirectory(courseDir, sectionDto.getTitle());
            
            List<String> lessonIds = processLessons(sectionDto.getLessons(), sectionDir, entitiesHolder);
            
            SectionRedisEntity sectionEntity = SectionRedisEntity.builder()
                    .id(sectionId)
                    .title(sectionDto.getTitle())
                    .lessonIds(lessonIds)
                    .build();
            entitiesHolder.addSection(sectionEntity);
            sectionIds.add(sectionId);
        }
        
        return sectionIds;
    }

    private Path createSectionDirectory(Path courseDir, String sectionTitle) {
        Path sectionDir = Paths.get(courseDir.toString(), sanitizeTitle(sectionTitle));
        try {
            Files.createDirectories(sectionDir);
        } catch (IOException e) {
            throw new InternalServerError("Failed to create section directory");
        }
        return sectionDir;
    }

    private List<String> processLessons(List<LessonInitRequest> lessonsRequest, Path sectionDir, CourseEntitiesHolder entitiesHolder) {
        List<String> lessonIds = new ArrayList<>();
        
        for (LessonInitRequest lessonDto : lessonsRequest) {
            String lessonId = "lesson_" + UUID.randomUUID().toString();
            Path lessonDir = createLessonDirectory(sectionDir, lessonDto.getTitle());
            
            List<String> resourceIds = processResources(lessonDto.getResources(), lessonDir, entitiesHolder);
            
            LessonRedisEntity lessonEntity = LessonRedisEntity.builder()
                    .id(lessonId)
                    .title(lessonDto.getTitle())
                    .durationMinutes(lessonDto.getDurationMinutes())
                    .isPreview(lessonDto.getIsPreview())
                    .resourceIds(resourceIds)
                    .build();
            entitiesHolder.addLesson(lessonEntity);
            lessonIds.add(lessonId);
        }
        
        return lessonIds;
    }

    private Path createLessonDirectory(Path sectionDir, String lessonTitle) {
        Path lessonDir = Paths.get(sectionDir.toString(), sanitizeTitle(lessonTitle));
        try {
            Files.createDirectories(lessonDir);
        } catch (IOException e) {
            throw new InternalServerError("Failed to create lesson directory");
        }
        return lessonDir;
    }

    private List<String> processResources(List<ResourceInitRequest> resourcesRequest, Path lessonDir, CourseEntitiesHolder entitiesHolder) {
        List<String> resourceIds = new ArrayList<>();
        
        for (ResourceInitRequest resourceDto : resourcesRequest) {
            String resourceId = "resource_" + UUID.randomUUID().toString();
            createResourceDirectory(lessonDir, resourceDto.getTitle());
            
            ResourceRedisEntity resourceEntity = ResourceRedisEntity.builder()
                    .id(resourceId)
                    .title(resourceDto.getTitle())
                    .isPreview(resourceDto.getIsPreview())
                    .resourceUrl(resourceDto.getResourceUrl())    
                    .build();
            entitiesHolder.addResource(resourceEntity);
            resourceIds.add(resourceId);
        }
        
        return resourceIds;
    }

    private void createResourceDirectory(Path lessonDir, String resourceTitle) {
        Path resourceDir = Paths.get(lessonDir.toString(), sanitizeTitle(resourceTitle));
        try {
            Files.createDirectories(resourceDir);
        } catch (IOException e) {
            throw new InternalServerError("Failed to create resource directory");
        }
    }

    private void saveAllEntities(CourseEntitiesHolder entitiesHolder) {
        resourcesRedisRepository.saveAll(entitiesHolder.getResources());
        lessonRedisRepository.saveAll(entitiesHolder.getLessons());
        sectionsRedisRepository.saveAll(entitiesHolder.getSections());
    }

    private void saveCourseEntity(CourseInitRequest request, Integer instructorId, String courseId, List<String> sectionIds) {
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
    }

    public String storeCourseImage(String courseId, MultipartFile imageFile, Integer instructorId) {
        // Validate image file
        if (imageFile.isEmpty()) {
            throw new BadRequestException("Image file is empty");
        }
        
        // Validate file type
        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("File must be an image");
        }
        
        // Validate file size (e.g., max 5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (imageFile.getSize() > maxSize) {
            throw new BadRequestException("Image file size must be less than 5MB");
        }
        
        // Check if course exists in Redis
        CourseRedisEntity courseEntity = courseRedisRepository.findById(courseId)
            .orElseThrow(() -> new BadRequestException("Course not found"));
        
        // Verify that the course belongs to the instructor
        if (!courseEntity.getInstructorId().equals(instructorId)) {
            throw new BadRequestException("You don't have permission to upload image for this course");
        }
        
        try {
            // Get course directory using course title from Redis
            String sanitizedTitle = sanitizeTitle(courseEntity.getTitle());
            Path courseDir = Paths.get(uploadPath, sanitizedTitle);
            
            // Check if course directory exists
            if (!Files.exists(courseDir)) {
                throw new BadRequestException("Course directory not found");
            }
            
            // Generate unique filename with original extension
            String originalFilename = imageFile.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
            
            String filename = "course_image_" +  extension;
            Path imagePath = courseDir.resolve(filename);
            
            // Save the image file directly in course directory
            Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Update course entity in Redis with image path
            CourseRedisEntity updatedCourse = CourseRedisEntity.builder()
                .id(courseEntity.getId())
                .instructorId(courseEntity.getInstructorId())
                .title(courseEntity.getTitle())
                .subTitle(courseEntity.getSubTitle())
                .description(courseEntity.getDescription())
                .price(courseEntity.getPrice())
                .isFree(courseEntity.getIsFree())
                .language(courseEntity.getLanguage())
                .courseDurationMinutes(courseEntity.getCourseDurationMinutes())
                .level(courseEntity.getLevel())
                .subcategoryId(courseEntity.getSubcategoryId())
                .sectionIds(courseEntity.getSectionIds())
                .imagePath(filename) // Store image path in Redis
                .build();
            
            courseRedisRepository.save(updatedCourse);
            
            // Return relative path
            return filename;
            
        } catch (IOException e) {
            throw new InternalServerError("Failed to store course image: " + e.getMessage());
        }
    }

    public String uploadFileToTmp(MultipartFile file) {
        // Validate file
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        
        // Validate file size (e.g., max 5GB)
        long maxSize = 5L * 1024 * 1024 * 1024; // 5GB
        if (file.getSize() > maxSize) {
            throw new BadRequestException("File size must be less than 5GB");
        }
        
        try {
            // Get original filename or create one if null
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.trim().isEmpty()) {
                originalFilename = "uploaded_file";
            }
            
            // Generate unique filename with timestamp
            String timestamp = String.valueOf(System.currentTimeMillis());
            String extension = originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
            
            String filename = "upload_" + timestamp + extension;
            Path filePath = Paths.get("/tmp", filename);
            
            // Save the file to /tmp directory
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Return the full path
            return filePath.toString();
            
        } catch (IOException e) {
            throw new InternalServerError("Failed to store file: " + e.getMessage());
        }
    }
    
    
    private static class CourseEntitiesHolder {
        private final List<ResourceRedisEntity> resources = new ArrayList<>();
        private final List<LessonRedisEntity> lessons = new ArrayList<>();
        private final List<SectionRedisEntity> sections = new ArrayList<>();

        public void addResource(ResourceRedisEntity resource) {
            resources.add(resource);
        }

        public void addLesson(LessonRedisEntity lesson) {
            lessons.add(lesson);
        }

        public void addSection(SectionRedisEntity section) {
            sections.add(section);
        }

        public List<ResourceRedisEntity> getResources() {
            return resources;
        }

        public List<LessonRedisEntity> getLessons() {
            return lessons;
        }

        public List<SectionRedisEntity> getSections() {
            return sections;
        }
    }
 

    private String sanitizeTitle(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "untitled";
        }
        // Replace spaces with underscores and remove special characters
        return fileName.trim()
                   .replaceAll("\\s+", "_")
                   .replaceAll("[^a-zA-Z0-9._-]", "")
                   .toLowerCase();
    }

    private void isValidPriceScale(String priceString) {
        try {
            BigDecimal price = new BigDecimal(priceString);
            if (price.scale() != 2) {
                throw new BadRequestException("invalid price");
            }
        } catch (NumberFormatException | NullPointerException e) {
            throw new BadRequestException("invalid price");
        }
    }
}
