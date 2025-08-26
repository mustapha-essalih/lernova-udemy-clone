package dev.api.courses;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import dev.api.courses.model.CourseDocument;
import dev.api.courses.model.SuggestionDocument;
import dev.api.courses.repository.CourseDocumentRepository;
import dev.api.courses.repository.SuggestionDocumentRepository;
import dev.api.courses.requests.CourseDocumentRequest;

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
