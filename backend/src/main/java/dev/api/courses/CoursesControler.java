package dev.api.courses;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.api.courses.model.CourseDocument;
import dev.api.courses.repository.CourseDocumentRepository;
import dev.api.courses.requests.CourseDocumentRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor
@RequestMapping("/api/v1/courses")
@RestController
public class CoursesControler {

    private ElasticsearchOperations elasticsearchOperations;
    private CourseDocumentRepository courseDocumentRepository;

    @GetMapping("/all")
    public Iterable<CourseDocument> getAllCourses() {
        return courseDocumentRepository.findAll();
    }

    // find by

    @GetMapping()
    public String getMethodName(@RequestParam String param) {

        return new String();
    }

    @PostMapping("/")
    public ResponseEntity<?> storeCourseDocument(@RequestBody List<CourseDocumentRequest> request) {

        List<CourseDocument> courseDocuments = new ArrayList<>();
        for (CourseDocumentRequest courseDocumentRequest : request) {
            CourseDocument courseDocument = new CourseDocument(
                    courseDocumentRequest.getTitle(),
                    courseDocumentRequest.getSubtitle(),
                    courseDocumentRequest.getDescription(),
                    courseDocumentRequest.getInstructorName(),
                    courseDocumentRequest.getCategory(),
                    courseDocumentRequest.getSubCategory(),
                    courseDocumentRequest.getLevel(),
                    courseDocumentRequest.getLanguage(),
                    courseDocumentRequest.getPrice(),
                    courseDocumentRequest.getAverageRating(),
                    courseDocumentRequest.getNumReviews(),
                    courseDocumentRequest.getNumStudents());
            courseDocuments.add(courseDocument);
        }

        courseDocumentRepository.saveAll(courseDocuments);

        return ResponseEntity.status(201).build();
    }

}
