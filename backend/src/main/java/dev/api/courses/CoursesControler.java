package dev.api.courses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.api.courses.model.CourseDocument;
import dev.api.courses.model.SuggestionDocument;
import dev.api.courses.repository.CourseDocumentRepository;
import dev.api.courses.repository.SuggestionDocumentRepository;
import dev.api.courses.requests.CourseDocumentRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/api/v1/courses")
@RestController
public class CoursesControler {

    private CourseDocumentRepository courseDocumentRepository;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private WebClient webClient;

    private ElasticsearchOperations elasticsearchOperations;
    private SuggestionDocumentRepository suggestionDocumentRepository;
 
    public CoursesControler(WebClient.Builder webClient, ElasticsearchOperations elasticsearchOperations,
            SuggestionDocumentRepository suggestionDocumentRepository,
            CourseDocumentRepository courseDocumentRepository) {
        this.courseDocumentRepository = courseDocumentRepository;
        this.webClient = webClient.build();
        this.elasticsearchOperations = elasticsearchOperations;
        this.suggestionDocumentRepository = suggestionDocumentRepository;
    }


    


    

    private String generateSuggestions(String title, String subtitle, String category, String subcategory,
            String instructor) throws JsonMappingException, JsonProcessingException {

        String question = String.format(
                """
                        You are an AI assistant that generates realistic and relevant search suggestions for an online course search engine like Udemy.
                        Given the following course information:
                        - Title: %s
                        - Subtitle: %s
                        - Category: %s
                        - Subcategory: %s

                        Generate exactly 15 short and unique search phrases (2–5 words each) that a user might type to find this course.
                        Rules:
                        1. Output ONLY the array of suggestions in JSON format, with no extra text or explanations.
                        2. Include variations with and without category keywords.
                        3. Use lowercase unless a proper noun is required.
                        4. No duplicate phrases.
                        5. Keep each suggestion concise and clear.

                        Return format:
                        ["suggestion 1", "suggestion 2", ...]
                        """,
                title,
                subtitle,
                category,
                subcategory);

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[] {
                                Map.of("text", question)
                        })
                });

        String response = webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            System.out.println("OK");
        ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.readTree(response);

        JsonNode textNode = rootNode.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text");

        if (textNode != null) {
            String textWithMarkdown = textNode.asText();
            String cleanedJson = textWithMarkdown.replace("```json\n", "").replace("```", "").replace("\"", "")
                    .replace("[", "").replace("]", "");
            cleanedJson += "," + instructor;
            return cleanedJson;
        }

        throw null;

    }

    @PostMapping
    public ResponseEntity<String> indexCourseDocument(@RequestBody CourseDocumentRequest request)
            throws JsonMappingException, JsonProcessingException {

        CourseDocument document = new CourseDocument();
        document.setTitle(request.getTitle());
        document.setSubtitle(request.getSubtitle());
        document.setDescription(request.getDescription());
        document.setInstructor(request.getInstructor());
        document.setCategory(request.getCategory());
        document.setSubCategory(request.getSubCategory());
        document.setLevel(request.getLevel());
        document.setLanguage(request.getLanguage());
        document.setPrice(request.getPrice());
        document.setAverageRating(request.getAverageRating());
        document.setNumReviews(request.getNumReviews());
        document.setNumStudents(request.getNumStudents());
        document.setFree(request.isFree());
        document.setVideoDuration(request.getVideoDuration());

        List<SuggestionDocument> inpuList = new ArrayList<>();
        try {
            String suggestions = generateSuggestions(request.getTitle(), request.getSubtitle(), request.getCategory(),
                    request.getSubCategory(), request.getInstructor()).trim();
            if (suggestions != null) {

                Completion completion = new Completion();

                String input[] = suggestions.split(",");

                for (int i = 0; i < input.length; i++) {
                    input[i] = input[i].replace("\n", "").trim();
                    SuggestionDocument suggestionDocument = new SuggestionDocument();
                    suggestionDocument.setPhrase(input[i]);
                    inpuList.add(suggestionDocument);
                }
                completion.setInput(input);
                document.setSuggest(completion);

                courseDocumentRepository.save(document);
                suggestionDocumentRepository.saveAll(inpuList);
            } else
                return ResponseEntity.status(HttpStatus.SC_INSUFFICIENT_STORAGE).body(
                        "Failed to generate course search suggestions: An internal processing error occurred. Please try again later");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INSUFFICIENT_STORAGE).body(
                    "Failed to generate course search suggestions: An internal processing error occurred. Please try again later");
        }

        return ResponseEntity.status(201).build();
    }


}