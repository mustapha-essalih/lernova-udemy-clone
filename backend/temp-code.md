```java 
// ============= INSTRUCTOR CONTROLLERS =============

@RestController
@RequestMapping("/api/instructor/courses")
@PreAuthorize("hasRole('INSTRUCTOR')")
public class InstructorCourseController {
    
    private final InstructorCourseService instructorCourseService;
    
    public InstructorCourseController(InstructorCourseService instructorCourseService) {
        this.instructorCourseService = instructorCourseService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Course>> createCourse(
            @AuthenticationPrincipal Long instructorId,
            @Valid @RequestBody CourseCreateRequest request) {
        
        Course course = instructorCourseService.createCourse(instructorId, request);
        ApiResponse<Course> response = new ApiResponse<>(true, "Course created successfully", course);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Course>> updateCourse(
            @AuthenticationPrincipal Long instructorId,
            @PathVariable Long courseId,
            @Valid @RequestBody CourseUpdateRequest request) {
        
        Course course = instructorCourseService.updateCourse(instructorId, courseId, request);
        ApiResponse<Course> response = new ApiResponse<>(true, "Course updated successfully", course);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Object>> deleteCourse(
            @AuthenticationPrincipal Long instructorId,
            @PathVariable Long courseId) {
        
        instructorCourseService.deleteCourse(instructorId, courseId);
        ApiResponse<Object> response = new ApiResponse<>(true, "Course deleted successfully", null);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Course>> getCourse(
            @AuthenticationPrincipal Long instructorId,
            @PathVariable Long courseId) {
        
        Course course = instructorCourseService.getCourseByInstructor(instructorId, courseId);
        ApiResponse<Course> response = new ApiResponse<>(true, "Course retrieved successfully", course);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Course>>> getAllMyCourses(
            @AuthenticationPrincipal Long instructorId) {
        
        List<Course> courses = instructorCourseService.getAllCoursesByInstructor(instructorId);
        ApiResponse<List<Course>> response = new ApiResponse<>(true, "Courses retrieved successfully", courses);
        return ResponseEntity.ok(response);
    }
}

// ============= MANAGER CONTROLLERS =============

@RestController
@RequestMapping("/api/manager/course-review")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerCourseReviewController {
    
    private final ManagerCourseService managerCourseService;
    
    public ManagerCourseReviewController(ManagerCourseService managerCourseService) {
        this.managerCourseService = managerCourseService;
    }
    
    @PutMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Course>> reviewCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseReviewRequest request) {
        
        Course course = managerCourseService.reviewCourse(courseId, request);
        String message = request.getStatus() == CourseStatus.PUBLISHED ? 
            "Course approved and published" : "Course reviewed successfully";
        ApiResponse<Course> response = new ApiResponse<>(true, message, course);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<Course>>> getPendingCourses() {
        List<Course> courses = managerCourseService.getPendingCourses();
        ApiResponse<List<Course>> response = new ApiResponse<>(true, "Pending courses retrieved", courses);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Course>> getCourseForReview(@PathVariable Long courseId) {
        Course course = managerCourseService.getCourseForReview(courseId);
        ApiResponse<Course> response = new ApiResponse<>(true, "Course details retrieved", course);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Course>>> getCoursesByStatus(
            @PathVariable CourseStatus status) {
        
        List<Course> courses = managerCourseService.getCoursesWithStatus(status);
        ApiResponse<List<Course>> response = new ApiResponse<>(true, 
            "Courses with status " + status + " retrieved", courses);
        return ResponseEntity.ok(response);
    }
}

// ============= ADMIN CONTROLLERS =============

@RestController
@RequestMapping("/api/admin/courses")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCourseController {
    
    private final AdminCourseService adminCourseService;
    
    public AdminCourseController(AdminCourseService adminCourseService) {
        this.adminCourseService = adminCourseService;
    }
    
    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Object>> deleteCourse(@PathVariable Long courseId) {
        adminCourseService.deleteCourse(courseId);
        ApiResponse<Object> response = new ApiResponse<>(true, "Course permanently deleted", null);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Course>> getCourse(@PathVariable Long courseId) {
        Course course = adminCourseService.getCourse(courseId);
        ApiResponse<Course> response = new ApiResponse<>(true, "Course retrieved", course);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Course>>> getAllCourses(
            @RequestParam(required = false) CourseStatus status) {
        
        List<Course> courses = status != null ? 
            adminCourseService.getCoursesByStatus(status) : 
            adminCourseService.getAllCourses();
        
        String message = status != null ? 
            "Courses with status " + status + " retrieved" : 
            "All courses retrieved";
            
        ApiResponse<List<Course>> response = new ApiResponse<>(true, message, courses);
        return ResponseEntity.ok(response);
    }
}

@RestController
@RequestMapping("/api/admin/course-review")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCourseReviewController {
    
    private final AdminCourseService adminCourseService;
    
    public AdminCourseReviewController(AdminCourseService adminCourseService) {
        this.adminCourseService = adminCourseService;
    }
    
    @PutMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Course>> reviewCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseReviewRequest request) {
        
        Course course = adminCourseService.reviewCourse(courseId, request);
        String message = request.getStatus() == CourseStatus.PUBLISHED ? 
            "Course approved and published" : "Course status updated";
        ApiResponse<Course> response = new ApiResponse<>(true, message, course);
        return ResponseEntity.ok(response);
    }
}

// ============= STUDENT CONTROLLERS =============

@RestController
@RequestMapping("/api/student/enrollments")
@PreAuthorize("hasRole('STUDENT')")
public class StudentEnrollmentController {
    
    private final StudentEnrollmentService studentEnrollmentService;
    
    public StudentEnrollmentController(StudentEnrollmentService studentEnrollmentService) {
        this.studentEnrollmentService = studentEnrollmentService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Enrollment>> enrollInCourse(
            @AuthenticationPrincipal Long studentId,
            @Valid @RequestBody EnrollmentRequest request) {
        
        Enrollment enrollment = studentEnrollmentService.enrollInCourse(studentId, request);
        ApiResponse<Enrollment> response = new ApiResponse<>(true, 
            "Successfully enrolled in course", enrollment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<Object>> unenrollFromCourse(
            @AuthenticationPrincipal Long studentId,
            @PathVariable Long courseId) {
        
        studentEnrollmentService.unenrollFromCourse(studentId, courseId);
        ApiResponse<Object> response = new ApiResponse<>(true, 
            "Successfully unenrolled from course", null);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Enrollment>>> getMyEnrollments(
            @AuthenticationPrincipal Long studentId) {
        
        List<Enrollment> enrollments = studentEnrollmentService.getStudentEnrollments(studentId);
        ApiResponse<List<Enrollment>> response = new ApiResponse<>(true, 
            "Your enrollments retrieved", enrollments);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/courses/{courseId}/status")
    public ResponseEntity<ApiResponse<Boolean>> checkEnrollmentStatus(
            @AuthenticationPrincipal Long studentId,
            @PathVariable Long courseId) {
        
        boolean isEnrolled = studentEnrollmentService.isEnrolledInCourse(studentId, courseId);
        ApiResponse<Boolean> response = new ApiResponse<>(true, 
            "Enrollment status checked", isEnrolled);
        return ResponseEntity.ok(response);
    }
}

@RestController
@RequestMapping("/api/student/ratings")
@PreAuthorize("hasRole('STUDENT')")
public class StudentRatingController {
    
    private final StudentRatingService studentRatingService;
    
    public StudentRatingController(StudentRatingService studentRatingService) {
        this.studentRatingService = studentRatingService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Rating>> rateCourse(
            @AuthenticationPrincipal Long studentId,
            @Valid @RequestBody RatingRequest request) {
        
        Rating rating = studentRatingService.rateCourse(studentId, request);
        ApiResponse<Rating> response = new ApiResponse<>(true, 
            "Course rated successfully", rating);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<Rating>> updateRating(
            @AuthenticationPrincipal Long studentId,
            @PathVariable Long courseId,
            @Valid @RequestBody RatingRequest request) {
        
        Rating rating = studentRatingService.updateRating(studentId, courseId, request);
        ApiResponse<Rating> response = new ApiResponse<>(true, 
            "Rating updated successfully", rating);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<Object>> deleteRating(
            @AuthenticationPrincipal Long studentId,
            @PathVariable Long courseId) {
        
        studentRatingService.deleteRating(studentId, courseId);
        ApiResponse<Object> response = new ApiResponse<>(true, 
            "Rating deleted successfully", null);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Rating>>> getMyRatings(
            @AuthenticationPrincipal Long studentId) {
        
        List<Rating> ratings = studentRatingService.getStudentRatings(studentId);
        ApiResponse<List<Rating>> response = new ApiResponse<>(true, 
            "Your ratings retrieved", ratings);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<Rating>> getMyRatingForCourse(
            @AuthenticationPrincipal Long studentId,
            @PathVariable Long courseId) {
        
        Rating rating = studentRatingService.getStudentRatingForCourse(studentId, courseId);
        ApiResponse<Rating> response = new ApiResponse<>(true, 
            "Your rating for this course retrieved", rating);
        return ResponseEntity.ok(response);
    }
}

// ============= PUBLIC CONTROLLERS =============

@RestController
@RequestMapping("/api/courses")
public class PublicCourseController {
    
    private final CourseViewService courseViewService;
    
    public PublicCourseController(CourseViewService courseViewService) {
        this.courseViewService = courseViewService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Course>>> getPublishedCourses() {
        List<Course> courses = courseViewService.getPublishedCourses();
        ApiResponse<List<Course>> response = new ApiResponse<>(true, 
            "Published courses retrieved", courses);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Course>> getCourseDetails(@PathVariable Long courseId) {
        Course course = courseViewService.getPublishedCourse(courseId);
        ApiResponse<Course> response = new ApiResponse<>(true, 
            "Course details retrieved", course);
        return ResponseEntity.ok(response);
    }
}

@RestController
@RequestMapping("/api/courses/{courseId}/ratings")
public class PublicCourseRatingController {
    
    private final CourseViewService courseViewService;
    
    public PublicCourseRatingController(CourseViewService courseViewService) {
        this.courseViewService = courseViewService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Rating>>> getCourseRatings(@PathVariable Long courseId) {
        List<Rating> ratings = courseViewService.getCourseRatings(courseId);
        ApiResponse<List<Rating>> response = new ApiResponse<>(true, 
            "Course ratings retrieved", ratings);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/average")
    public ResponseEntity<ApiResponse<Double>> getCourseAverageRating(@PathVariable Long courseId) {
        Double averageRating = courseViewService.getCourseAverageRating(courseId);
        ApiResponse<Double> response = new ApiResponse<>(true, 
            "Average rating retrieved", averageRating);
        return ResponseEntity.ok(response);
    }
}

// ============= SEARCH CONTROLLER (Existing) =============

@RestController
@RequestMapping("/api/search")
public class CourseSearchController {
    
    private final CourseSearchService courseSearchService; // Your existing search service
    
    public CourseSearchController(CourseSearchService courseSearchService) {
        this.courseSearchService = courseSearchService;
    }
    
    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<Course>>> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Use your existing search implementation
        List<Course> courses = courseSearchService.searchCourses(
            keyword, category, minPrice, maxPrice, minRating, page, size);
        
        ApiResponse<List<Course>> response = new ApiResponse<>(true, 
            "Search results retrieved", courses);
        return ResponseEntity.ok(response);
    }
}
```





















```java

// 1. Entity Classes
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();
    
    // constructors, getters, setters
}

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer orderIndex;
    
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    private List<Lesson> lessons = new ArrayList<>();
    
    // constructors, getters, setters
}

@Entity
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Integer orderIndex;
    
    @Enumerated(EnumType.STRING)
    private ContentType contentType;
    
    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;
    
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<LessonContent> contents = new ArrayList<>();
    
    // constructors, getters, setters
}

@Entity
public class LessonContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String originalFileName;
    private String fileUrl;
    private String mimeType;
    private Long fileSize;
    private Integer orderIndex;
    
    @Enumerated(EnumType.STRING)
    private ContentType contentType;
    
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    
    // constructors, getters, setters
}

enum ContentType {
    VIDEO, PDF, IMAGE, TEXT
}

// 2. File Storage Service
@Service
public class FileStorageService {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    private final Path fileStorageLocation;
    
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }
    
    public String storeFile(MultipartFile file, Long courseId, Long sectionId, Long lessonId) {
        validateFile(file);
        
        String fileName = generateFileName(file, courseId, sectionId, lessonId);
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file " + fileName, ex);
        }
    }
    
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + fileName, ex);
        }
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Size validation (100MB for videos, 10MB for others)
        long maxSize = file.getContentType().startsWith("video/") ? 100 * 1024 * 1024 : 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed");
        }
        
        // Type validation
        String contentType = file.getContentType();
        if (!isAllowedContentType(contentType)) {
            throw new IllegalArgumentException("File type not allowed: " + contentType);
        }
    }
    
    private boolean isAllowedContentType(String contentType) {
        return contentType.startsWith("video/") || 
               contentType.equals("application/pdf") ||
               contentType.startsWith("image/");
    }
    
    private String generateFileName(MultipartFile file, Long courseId, Long sectionId, Long lessonId) {
        String extension = getFileExtension(file.getOriginalFilename());
        String timestamp = String.valueOf(System.currentTimeMillis());
        return String.format("course_%d/section_%d/lesson_%d/%s_%s.%s",
                courseId, sectionId, lessonId, timestamp, UUID.randomUUID().toString(), extension);
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}

// 3. Upload Controller
@RestController
@RequestMapping("/api")
@CrossOrigin
public class CourseUploadController {
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private CourseService courseService;
    
    // Single file upload per lesson
    @PostMapping("/courses/{courseId}/sections/{sectionId}/lessons/{lessonId}/upload")
    public ResponseEntity<ApiResponse> uploadFile(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long lessonId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("contentType") ContentType contentType,
            @RequestParam(value = "orderIndex", defaultValue = "0") Integer orderIndex) {
        
        try {
            // Store file
            String fileName = fileStorageService.storeFile(file, courseId, sectionId, lessonId);
            
            // Save metadata to database
            LessonContent content = new LessonContent();
            content.setFileName(fileName);
            content.setOriginalFileName(file.getOriginalFilename());
            content.setFileUrl("/api/files/" + fileName);
            content.setMimeType(file.getContentType());
            content.setFileSize(file.getSize());
            content.setContentType(contentType);
            content.setOrderIndex(orderIndex);
            
            courseService.addContentToLesson(lessonId, content);
            
            return ResponseEntity.ok(new ApiResponse(true, "File uploaded successfully", content));
            
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, ex.getMessage(), null));
        }
    }
    
    // Batch upload for a section
    @PostMapping("/courses/{courseId}/sections/{sectionId}/upload-batch")
    public ResponseEntity<ApiResponse> uploadMultipleFiles(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("lessonIds") Long[] lessonIds,
            @RequestParam("contentTypes") ContentType[] contentTypes) {
        
        if (files.length != lessonIds.length || files.length != contentTypes.length) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Arrays length mismatch", null));
        }
        
        List<LessonContent> uploadedContents = new ArrayList<>();
        
        try {
            for (int i = 0; i < files.length; i++) {
                String fileName = fileStorageService.storeFile(files[i], courseId, sectionId, lessonIds[i]);
                
                LessonContent content = new LessonContent();
                content.setFileName(fileName);
                content.setOriginalFileName(files[i].getOriginalFilename());
                content.setFileUrl("/api/files/" + fileName);
                content.setMimeType(files[i].getContentType());
                content.setFileSize(files[i].getSize());
                content.setContentType(contentTypes[i]);
                content.setOrderIndex(i);
                
                courseService.addContentToLesson(lessonIds[i], content);
                uploadedContents.add(content);
            }
            
            return ResponseEntity.ok(new ApiResponse(true, "All files uploaded successfully", uploadedContents));
            
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, ex.getMessage(), null));
        }
    }
    
    // File download/streaming
    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        
        String contentType = "application/octet-stream";
        try {
            contentType = Files.probeContentType(Paths.get(resource.getFile().getAbsolutePath()));
        } catch (IOException ex) {
            // Could not determine file type
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    // Progress tracking for large uploads
    @PostMapping("/courses/{courseId}/sections/{sectionId}/lessons/{lessonId}/upload-chunked")
    public ResponseEntity<ApiResponse> uploadChunkedFile(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long lessonId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("chunkIndex") Integer chunkIndex,
            @RequestParam("totalChunks") Integer totalChunks,
            @RequestParam("originalFileName") String originalFileName) {
        
        // Implementation for chunked upload (for large videos)
        // This is more complex and typically requires temporary storage
        // and reassembly of chunks
        
        return ResponseEntity.ok(new ApiResponse(true, "Chunk uploaded", null));
    }
}

// 4. Course Service
@Service
@Transactional
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SectionRepository sectionRepository;
    
    @Autowired
    private LessonRepository lessonRepository;
    
    @Autowired
    private LessonContentRepository contentRepository;
    
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }
    
    public Section addSectionToCourse(Long courseId, Section section) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        section.setCourse(course);
        return sectionRepository.save(section);
    }
    
    public Lesson addLessonToSection(Long sectionId, Lesson lesson) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));
        
        lesson.setSection(section);
        return lessonRepository.save(lesson);
    }
    
    public LessonContent addContentToLesson(Long lessonId, LessonContent content) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        content.setLesson(lesson);
        return contentRepository.save(content);
    }
    
    public Course getCourseWithContent(Long courseId) {
        return courseRepository.findByIdWithSectionsAndLessons(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }
}

// 5. Configuration
@Configuration
public class FileUploadConfig {
    
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse("100MB"));
        factory.setMaxRequestSize(DataSize.parse("500MB"));
        return factory.createMultipartConfig();
    }
}

// 6. API Response DTO
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
    
    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    // getters and setters
}

```