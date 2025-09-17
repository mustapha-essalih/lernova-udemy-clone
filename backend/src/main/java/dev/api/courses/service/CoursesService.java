package dev.api.courses.service;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import dev.api.courses.repository.CourseDocumentRepository;
import dev.api.courses.repository.SuggestionDocumentRepository;

@Service
public class CoursesService {
    
    private CourseDocumentRepository courseDocumentRepository;
    private WebClient webClient;
    private ElasticsearchOperations elasticsearchOperations;
    private SuggestionDocumentRepository suggestionDocumentRepository;
 
    public CoursesService(WebClient.Builder webClient, ElasticsearchOperations elasticsearchOperations,
            SuggestionDocumentRepository suggestionDocumentRepository,
            CourseDocumentRepository courseDocumentRepository) {
        this.courseDocumentRepository = courseDocumentRepository;
        this.webClient = webClient.build();
        this.elasticsearchOperations = elasticsearchOperations;
        this.suggestionDocumentRepository = suggestionDocumentRepository;
    }
     


}
