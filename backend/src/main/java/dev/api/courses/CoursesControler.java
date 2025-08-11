package dev.api.courses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.elasticsearch.client.RequestOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.data.elasticsearch.core.suggest.response.CompletionSuggestion;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest.Suggestion;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest.Suggestion.Entry;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest.Suggestion.Entry.Option;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggester;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.SuggestFuzziness;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import co.elastic.clients.elasticsearch.core.search.SuggestionBuilders;
import dev.api.courses.model.CourseDocument;
import dev.api.courses.repository.CourseDocumentRepository;
import dev.api.courses.requests.CourseDocumentRequest;
import dev.api.exceptions.InternalServerError;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
    private ElasticsearchClient elasticsearchClient;

    public CoursesControler(WebClient.Builder webClient, ElasticsearchOperations elasticsearchOperations,
            ElasticsearchClient elasticsearchClient,
            CourseDocumentRepository courseDocumentRepository) {
        this.courseDocumentRepository = courseDocumentRepository;
        this.webClient = webClient.build();
        this.elasticsearchClient = elasticsearchClient;
        this.elasticsearchOperations = elasticsearchOperations;
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

    @PostMapping("/")
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

        try {
            String suggestions = generateSuggestions(request.getTitle(), request.getSubtitle(), request.getCategory(),
                    request.getSubCategory(), request.getInstructor()).trim();
            if (suggestions != null) {

                Completion completion = new Completion();

                String input[] = suggestions.split(",");

                for (int i = 0; i < input.length; i++) {
                    input[i] = input[i].replace("\n", "").trim();
                }
                completion.setInput(input);
                document.setSuggest(completion);

                courseDocumentRepository.save(document);
            } else
                return ResponseEntity.status(HttpStatus.SC_INSUFFICIENT_STORAGE).body(
                        "Failed to generate course search suggestions: An internal processing error occurred. Please try again later");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INSUFFICIENT_STORAGE).body(
                    "Failed to generate course search suggestions: An internal processing error occurred. Please try again later");
        }

        return ResponseEntity.status(201).build();
    }
 
    @GetMapping("/get")
    public List<? extends Entry<? extends Option>> getCourseSuggestions(@RequestParam String prefix) {
        SuggestFuzziness fuzziness = SuggestFuzziness.of(f -> f
                .fuzziness("AUTO") 
                .transpositions(true));

        
        CompletionSuggester completionSuggester = CompletionSuggester.of(c -> c
                .field("suggest")
                .size(10)
                .skipDuplicates(true)
                .fuzzy(fuzziness));

        
        FieldSuggester fieldSuggester = FieldSuggester.of(fs -> fs
                .prefix(prefix)
                .completion(completionSuggester));
        NativeQuery searchQuery = new NativeQueryBuilder().withSuggester(Suggester.of(s -> s
                .suggesters("course_suggest", fieldSuggester)))
                .build();

        SearchHits<CourseDocument> articles = elasticsearchOperations.search(searchQuery, CourseDocument.class,
                IndexCoordinates.of("courses"));

        Suggest suggest = articles.getSuggest();
        Suggestion<? extends Entry<? extends Option>> completionSuggestion = suggest.getSuggestion("course_suggest");

        return completionSuggestion.getEntries();
    }


    @GetMapping("/test")
public Object getCourseSuggestion(@RequestParam String prefix) {
    SuggestFuzziness fuzziness = SuggestFuzziness.of(f -> f
            .fuzziness("AUTO")
            .transpositions(true));

    CompletionSuggester completionSuggester = CompletionSuggester.of(c -> c
            .field("suggest")
            .size(10)
            .skipDuplicates(true)
            .fuzzy(fuzziness));

    FieldSuggester fieldSuggester = FieldSuggester.of(fs -> fs
            .prefix(prefix)
            .completion(completionSuggester));

    NativeQuery suggestQuery = new NativeQueryBuilder()
            .withSuggester(Suggester.of(s -> s
                    .suggesters("course_suggest", fieldSuggester)))
            .build();

    SearchHits<CourseDocument> suggestHits = elasticsearchOperations.search(suggestQuery, CourseDocument.class,
            IndexCoordinates.of("courses"));

    Suggest suggest = suggestHits.getSuggest();
    Suggestion<? extends Entry<? extends Option>> completionSuggestion = suggest.getSuggestion("course_suggest");

    // Check if suggestions exist
    if (completionSuggestion != null && !completionSuggestion.getEntries().isEmpty()) {
        // Return suggestions entries (autocomplete results)
        return completionSuggestion.getEntries();
    } else {
        // No suggestions, fallback to full-text search on title
        NativeQuery fullTextQuery = new NativeQueryBuilder()
            .withQuery(q -> q
                .match(m -> m
                    .field("title")
                    .query(prefix)
                ))
            .build();

        SearchHits<CourseDocument> fullTextHits = elasticsearchOperations.search(fullTextQuery,
                CourseDocument.class, IndexCoordinates.of("courses"));

        // Return list of matched courses (or map to your desired response)
        return fullTextHits.getSearchHits();
    }
}


}
