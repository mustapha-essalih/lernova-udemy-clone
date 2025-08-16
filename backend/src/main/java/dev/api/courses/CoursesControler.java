package dev.api.courses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest.Suggestion;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest.Suggestion.Entry;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest.Suggestion.Entry.Option;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.ScoreSort;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhraseQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggester;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.SuggestFuzziness;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import dev.api.courses.model.CourseDocument;
import dev.api.courses.model.SuggestionDocument;
import dev.api.courses.repository.CourseDocumentRepository;
import dev.api.courses.repository.SuggestionDocumentRepository;
import dev.api.courses.requests.CourseDocumentRequest;
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

    @GetMapping("/suggestions")
    public Object getCourseSuggestions(@RequestParam String prefix) {

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
    public List<String> searchGeneralSuggestions(String searchInput) {

        String queryString = searchInput;
        String wildcardValue = "*" + searchInput.toLowerCase() + "*";

        Query matchQuery = MatchQuery.of(m -> m
                .field("phrase")
                .query(queryString)
                .fuzziness("AUTO"))._toQuery();

        Query wildcardQuery = WildcardQuery.of(w -> w
                .field("phrase")
                .value(wildcardValue)
                .boost(0.8f))._toQuery();

        Query boolQuery = BoolQuery.of(b -> b
                .should(matchQuery)
                .should(wildcardQuery)
                .minimumShouldMatch("1"))._toQuery();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(0, 5))
                .withSort(SortOptions.of(s -> s
                        .score(ScoreSort.of(ss -> ss.order(SortOrder.Desc)))))
                .build();

        SearchHits<SuggestionDocument> hits = elasticsearchOperations.search(searchQuery, SuggestionDocument.class,
                IndexCoordinates.of("general_suggestions"));

        return hits.getSearchHits().stream()
                .map(hit -> hit.getContent().getPhrase())
                .collect(Collectors.toList());
    }


    @GetMapping("/search")
    public Map<String, Object> searchJavascriptProgramming(
            @RequestParam String q,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) List<String> languages,
            @RequestParam(required = false) List<String> durations,
            @RequestParam(required = false) List<String> subcategories,
            @RequestParam(required = false) List<String> levels,
            @RequestParam(required = false) List<String> priceTypes,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
 

        MultiMatchQuery multiMatchQuery = buildMultiMatchQuery(q);

        List<Query> filters = buildFilters(rating, languages, durations, subcategories, levels, priceTypes);

        BoolQuery boolQuery = buildMainBoolQuery(q, multiMatchQuery, filters);

        NativeQuery searchQuery = buildSearchQuery(boolQuery, page, size);

        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(searchQuery, CourseDocument.class);

        List<CourseDocument> courses = searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
        long totalHits = searchHits.getTotalHits();

        Map<String, Object> pagination = buildPagination(page, size, totalHits);
        Map<String, Object> filterCounts = buildFilterCounts(courses);

        return buildResponse(courses, totalHits, pagination, filterCounts);
    }

    private MultiMatchQuery buildMultiMatchQuery(String q) {
        return MultiMatchQuery.of(m -> m
                .query(q)
                .fields("title^3", "title.trigram^2", "subtitle^2", "subtitle.trigram^1.5",
                        "description^1.5", "description.trigram^1.5")
                .type(TextQueryType.BestFields)
                .minimumShouldMatch("75%"));
    }

    private List<Query> buildFilters(Double rating, List<String> languages, List<String> durations,
            List<String> subcategories, List<String> levels, List<String> priceTypes) {
        List<Query> filters = new ArrayList<>();

        if (rating != null) {
            filters.add(RangeQuery.of(r -> r
                    .number(n -> n.field("averageRating").gte(rating)))
                    ._toQuery());
        }

        if (languages != null && !languages.isEmpty()) {
            filters.add(TermsQuery.of(t -> t
        .field("language")
        .terms(TermsQueryField.of(tf -> tf.value(
            languages.stream().map(FieldValue::of).collect(Collectors.toList())
        ))))
        ._toQuery());
    }

        if (durations != null && !durations.isEmpty()) {
            List<Query> durationQueries = durations.stream().map(d -> {
                String[] parts = d.split("-");
                double gte = Double.parseDouble(parts[0]);
                double lte = parts[1].equals("+") ? Double.MAX_VALUE : Double.parseDouble(parts[1]);
                return RangeQuery.of(r -> r.number(n -> n.field("videoDuration").gte(gte).lte(lte)))._toQuery();
            }).toList();
            filters.add(BoolQuery.of(b -> b.should(durationQueries))._toQuery());
        }

        if (subcategories != null && !subcategories.isEmpty()) {
        filters.add(TermsQuery.of(t -> t
         .field("subCategory")
         .terms(TermsQueryField.of(tf -> tf.value(
             subcategories.stream().map(FieldValue::of).collect(Collectors.toList())
         ))))
         ._toQuery());
        }


        if (levels != null && !levels.isEmpty()) {
            filters.add(TermsQuery.of(t -> t
            .field("level")
            .terms(TermsQueryField.of(tf -> tf.value(
                levels.stream().map(FieldValue::of).collect(Collectors.toList())
            ))))
            ._toQuery());
        }

        if (priceTypes != null && !priceTypes.isEmpty()) {
            List<Query> priceQueries = new ArrayList<>();
            for (String priceType : priceTypes) {
                if ("free".equalsIgnoreCase(priceType)) {
                    priceQueries.add(RangeQuery.of(r -> r.number(n -> n.field("price").lte(0.0)))._toQuery());
                } else if ("paid".equalsIgnoreCase(priceType)) {
                    priceQueries.add(RangeQuery.of(r -> r.number(n -> n.field("price").gt(0.0)))._toQuery());
                }
            }
            if (!priceQueries.isEmpty()) {
                filters.add(BoolQuery.of(b -> b.should(priceQueries))._toQuery());
            }
        }

        return filters;
    }

    private BoolQuery buildMainBoolQuery(String searchTerm, MultiMatchQuery multiMatchQuery, List<Query> filters) {
        MatchPhraseQuery titlePhrase = MatchPhraseQuery.of(mp -> mp.field("title").query(searchTerm).boost(2.0f));
        MatchPhraseQuery subtitlePhrase = MatchPhraseQuery.of(mp -> mp.field("subtitle").query(searchTerm).boost(1.5f));
        MatchPhraseQuery descriptionPhrase = MatchPhraseQuery
                .of(mp -> mp.field("description").query(searchTerm).boost(1.5f));

        return BoolQuery.of(b -> b
                .must(multiMatchQuery._toQuery())
                .filter(filters)
                .should(titlePhrase._toQuery())
                .should(subtitlePhrase._toQuery())
                .should(descriptionPhrase._toQuery()));
    }

    private NativeQuery buildSearchQuery(BoolQuery boolQuery, int page, int size) {
        return NativeQuery.builder()
                .withQuery(boolQuery._toQuery())
                .withSort(sort -> sort.score(s -> s.order(SortOrder.Desc)))
                .withSort(sort -> sort.field(f -> f.field("averageRating").order(SortOrder.Desc)))
                .withPageable(PageRequest.of(page, size))
                .build();
    }

    private Map<String, Object> buildPagination(int page, int size, long totalHits) {
        int totalPages = (int) Math.ceil((double) totalHits / size);
        boolean hasNext = page < totalPages - 1;
        boolean hasPrevious = page > 0;

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", page);
        pagination.put("size", size);
        pagination.put("totalPages", totalPages);
        pagination.put("hasNext", hasNext);
        pagination.put("hasPrevious", hasPrevious);
        return pagination;
    }

    private Map<String, Object> buildFilterCounts(List<CourseDocument> courses) {
        Map<String, Long> languageCount = courses.stream()
                .collect(Collectors.groupingBy(CourseDocument::getLanguage, Collectors.counting()));

        Map<String, Long> subCategoryCount = courses.stream()
                .collect(Collectors.groupingBy(CourseDocument::getSubCategory, Collectors.counting()));

        Map<String, Long> levelCount = courses.stream()
                .collect(Collectors.groupingBy(CourseDocument::getLevel, Collectors.counting()));

        Map<String, Long> durationCount = courses.stream().collect(Collectors.groupingBy(c -> {
            int d = c.getVideoDuration();
            if (d < 1)
                return "0-1";
            else if (d < 3)
                return "1-3";
            else
                return "3+";
        }, Collectors.counting()));

        Map<String, Long> priceTypeCount = courses.stream()
                .collect(Collectors.groupingBy(c -> c.getPrice() > 0 ? "paid" : "free", Collectors.counting()));

        Map<String, Object> filterCounts = new HashMap<>();
        filterCounts.put("languages", languageCount);
        filterCounts.put("subCategories", subCategoryCount);
        filterCounts.put("levels", levelCount);
        filterCounts.put("durations", durationCount);
        filterCounts.put("priceTypes", priceTypeCount);

        return filterCounts;
    }

    private Map<String, Object> buildResponse(List<CourseDocument> courses, long totalHits,
            Map<String, Object> pagination, Map<String, Object> filterCounts) {
        Map<String, Object> response = new HashMap<>();
        response.put("courses", courses);
        response.put("totalResults", totalHits);
        response.put("pagination", pagination);
        response.put("filterCounts", filterCounts);
        return response;
    }




    // @GetMapping("/search")
    // public List<CourseDocument> searchJavascriptProgramming() {
    // String searchTerm = "programming";

    // // Multi-match query (must)
    // MultiMatchQuery multiMatchQuery = MultiMatchQuery.of(m -> m
    // .query(searchTerm)
    // .fields("title^3", "title.trigram^2", "subtitle^2", "subtitle.trigram^1.5",
    // "description^1", "description.trigram^0.8")
    // .type(TextQueryType.BestFields)
    // .minimumShouldMatch("75%")
    // );

    // // Price range filter (0 to 100)
    // RangeQuery priceRangeQuery = RangeQuery.of(r -> r
    // .number(n -> n
    // .field("price")
    // .gte(0.0)
    // .lte(100.0)
    // )
    // );

    // // Should clauses
    // MatchPhraseQuery titlePhrase = MatchPhraseQuery.of(mp -> mp
    // .field("title")
    // .query(searchTerm)
    // .boost(2.0f)
    // );

    // MatchPhraseQuery subtitlePhrase = MatchPhraseQuery.of(mp -> mp
    // .field("subtitle")
    // .query(searchTerm)
    // .boost(1.5f)
    // );

    // MatchPhraseQuery descriptionPhrase = MatchPhraseQuery.of(mp -> mp
    // .field("description")
    // .query(searchTerm)
    // .boost(1.5f)
    // );

    // // Complete bool query
    // BoolQuery boolQuery = BoolQuery.of(b -> b
    // .must(multiMatchQuery._toQuery())
    // .filter(priceRangeQuery._toQuery())
    // .should(titlePhrase._toQuery())
    // .should(subtitlePhrase._toQuery())
    // .should(descriptionPhrase._toQuery())
    // );

    // // Build final query with sorting and pagination
    // NativeQuery searchQuery = NativeQuery.builder()
    // .withQuery(boolQuery._toQuery())
    // .withSort(sort -> sort.score(s -> s.order(SortOrder.Desc)))
    // .withSort(sort -> sort.field(f ->
    // f.field("averageRating").order(SortOrder.Desc)))
    // .withPageable(PageRequest.of(0, 20)) // from: 0, size: 20
    // .build();

    // SearchHits<CourseDocument> searchHits =
    // elasticsearchOperations.search(searchQuery, CourseDocument.class);
    // return searchHits.stream()
    // .map(SearchHit::getContent)
    // .collect(Collectors.toList());
    // }

    // @GetMapping("/search")
    // public Map<String, Object> searchJavascriptProgramming(
    // @RequestParam(required = false) Double rating,
    // @RequestParam(required = false) List<String> languages,
    // @RequestParam(required = false) List<String> durations,
    // @RequestParam(required = false) List<String> subcategories,
    // @RequestParam(required = false) List<String> levels) {

    // String searchTerm = "programming";

    // MultiMatchQuery multiMatchQuery = MultiMatchQuery.of(m -> m
    // .query(searchTerm)
    // .fields("title^3", "title.trigram^2", "subtitle^2", "subtitle.trigram^1.5",
    // "description^1", "description.trigram^0.8")
    // .type(TextQueryType.BestFields)
    // .minimumShouldMatch("75%"));

    // List<Query> filters = new ArrayList<>();

    // if (rating != null) {
    // filters.add(RangeQuery.of(r -> r
    // .number(n -> n
    // .field("averageRating")
    // .gte(rating))) // Greater than or equal to rating
    // ._toQuery());
    // }

    // if (languages != null && !languages.isEmpty()) {
    // filters.add(TermsQuery.of(t -> t
    // .field("language.keyword")
    // .terms(ts ->
    // ts.value(languages.stream().map(FieldValue::of).toList())))._toQuery());
    // }

    // if (durations != null && !durations.isEmpty()) {

    // List<Query> durationQueries = new ArrayList<>();
    // for (String duration : durations) {
    // String[] parts = duration.split("-");
    // double gte = Double.parseDouble(parts[0]);
    // double lte = Double.parseDouble(parts[1]);
    // durationQueries.add(RangeQuery.of(r -> r
    // .number(n -> n
    // .field("videoDuration")
    // .gte(gte)
    // .lte(lte)
    // )
    // )._toQuery());
    // }
    // filters.add(BoolQuery.of(b -> b.should(durationQueries))._toQuery());
    // }

    // if (subcategories != null && !subcategories.isEmpty()) {
    // filters.add(TermsQuery.of(t -> t
    // .field("subCategory.keyword")
    // .terms(ts ->
    // ts.value(subcategories.stream().map(FieldValue::of).toList())))._toQuery());
    // }

    // if (levels != null && !levels.isEmpty()) {
    // filters.add(TermsQuery.of(t -> t
    // .field("level.keyword")
    // .terms(ts ->
    // ts.value(levels.stream().map(FieldValue::of).toList())))._toQuery());
    // }

    // MatchPhraseQuery titlePhrase = MatchPhraseQuery.of(mp -> mp
    // .field("title")
    // .query(searchTerm)
    // .boost(2.0f));

    // MatchPhraseQuery subtitlePhrase = MatchPhraseQuery.of(mp -> mp
    // .field("subtitle")
    // .query(searchTerm)
    // .boost(1.5f));

    // MatchPhraseQuery descriptionPhrase = MatchPhraseQuery.of(mp -> mp
    // .field("description")
    // .query(searchTerm)
    // .boost(1.5f));

    // BoolQuery boolQuery = BoolQuery.of(b -> b
    // .must(multiMatchQuery._toQuery())
    // .filter(filters)
    // .should(titlePhrase._toQuery())
    // .should(subtitlePhrase._toQuery())
    // .should(descriptionPhrase._toQuery()));

    // NativeQuery searchQuery = NativeQuery.builder()
    // .withQuery(boolQuery._toQuery())
    // .withSort(sort -> sort.score(s -> s.order(SortOrder.Desc)))
    // .withSort(sort -> sort.field(f ->
    // f.field("averageRating").order(SortOrder.Desc)))
    // .withPageable(PageRequest.of(0, 20))
    // .build();

    // SearchHits<CourseDocument> searchHits =
    // elasticsearchOperations.search(searchQuery, CourseDocument.class);
    // List<CourseDocument> courses =
    // searchHits.stream().map(SearchHit::getContent).toList();

    // Map<String, Long> languageCount = courses.stream()
    // .collect(Collectors.groupingBy(CourseDocument::getLanguage,
    // Collectors.counting()));
    // Map<String, Long> subCategoryCount = courses.stream()
    // .collect(Collectors.groupingBy(CourseDocument::getSubCategory,
    // Collectors.counting()));
    // Map<String, Long> levelCount = courses.stream()
    // .collect(Collectors.groupingBy(CourseDocument::getLevel,
    // Collectors.counting()));

    // Map<String, Long> durationCount =
    // courses.stream().collect(Collectors.groupingBy(c -> {
    // double d = c.getVideoDuration();
    // if (d < 1) return "0-1";
    // else if (d < 3) return "1-3";
    // else return "3+";
    // }, Collectors.counting()));

    // Map<String, Object> response = new HashMap<>();
    // response.put("courses", courses);
    // response.put("languagesCount", languageCount);
    // response.put("subCategoriesCount", subCategoryCount);
    // response.put("levelsCount", levelCount);
    // response.put("durationsCount", durationCount);

    // return response;
    // }

    @GetMapping("/all")
    public Iterable<CourseDocument> getMethodName() {
        return this.courseDocumentRepository.findAll();
    }
    
}
