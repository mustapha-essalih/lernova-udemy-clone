package dev.api.courses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest.Suggestion;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest.Suggestion.Entry;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest.Suggestion.Entry.Option;
import org.springframework.stereotype.Service;
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
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggester;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.SuggestFuzziness;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import dev.api.common.ApiResponse;
import dev.api.courses.model.CourseDocument;
import dev.api.courses.model.SuggestionDocument;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class SearchService {

    private ElasticsearchOperations elasticsearchOperations;

    public ApiResponse<Map<String, Object>> searchCourses(String q, Double rating, List<String> languages, List<String> durations,
            List<String> subcategories, List<String> levels, List<String> priceTypes, int page, int size) {

        if (q == null || q.trim().length() < 2) {
            return new ApiResponse<Map<String,Object>>(true, null, null, 200);
        }

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

    public ApiResponse<List<String>> getSuggestions(String prefix) {
       
        if (prefix == null || prefix.trim().length() < 2) {
            return new ApiResponse<List<String>>(true, null, null, 200);
        }

        String wildcardValue = "*" + prefix.toLowerCase() + "*";

        Query matchQuery = MatchQuery.of(m -> m
                .field("phrase")
                .query(prefix)
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
                .withMaxResults(5)
                .withSort(SortOptions.of(s -> s
                        .score(ScoreSort.of(ss -> ss.order(SortOrder.Desc)))))
                .build();

        SearchHits<SuggestionDocument> hits = elasticsearchOperations.search(searchQuery, SuggestionDocument.class,
                IndexCoordinates.of("general_suggestions"));

         List<String> response = hits.getSearchHits().stream()
                .map(hit -> hit.getContent().getPhrase())
                .collect(Collectors.toList());
    
        return new ApiResponse<>(true, null, response, 200);
    }

    public ApiResponse<List<CourseDocument>> suggestInstructors(String prefix) {
        if (prefix == null || prefix.trim().length() < 2) {
            return new ApiResponse<List<CourseDocument>>(true, null, null, 200);
        }

        Query searchQuery = Query.of(q -> q
                .bool(BoolQuery.of(b -> b
                        .should(Query.of(sq -> sq
                                .match(m -> m
                                        .field("instructor")
                                        .query(prefix)
                                        .boost(5.0f))))
                        .should(Query.of(sq -> sq
                                .matchPhrasePrefix(mp -> mp
                                        .field("instructor")
                                        .query(prefix)
                                        .boost(4.0f))))
                        .should(Query.of(sq -> sq
                                .match(m -> m
                                        .field("instructor.trigram")
                                        .query(prefix)
                                        .boost(3.0f))))
                        .should(Query.of(sq -> sq
                                .fuzzy(f -> f
                                        .field("instructor")
                                        .value(prefix)
                                        .fuzziness("AUTO")
                                        .prefixLength(1)
                                        .maxExpansions(50)
                                        .boost(2.0f))))
                        .should(Query.of(sq -> sq
                                .match(m -> m
                                        .field("instructor.trigram")
                                        .query(prefix)
                                        .fuzziness("AUTO")
                                        .boost(1.5f))))
                        .should(Query.of(sq -> sq
                                .wildcard(w -> w
                                        .field("instructor")
                                        .value(prefix.toLowerCase() + "*")
                                        .boost(1.0f))))
                        .minimumShouldMatch("1"))));

        NativeQuery nativeQuery = new NativeQueryBuilder()
                .withQuery(searchQuery)
                .withMaxResults(3)
                .build();

        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(
                nativeQuery, CourseDocument.class, IndexCoordinates.of("courses"));

         List<CourseDocument> response = searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());

        return new ApiResponse<List<CourseDocument>>(true, null, response, 200);

    }

    public ApiResponse<Object> suggestCourseTitles(String prefix) {

        if (prefix == null || prefix.trim().length() < 2) {
            return new ApiResponse<Object>(true, null, null, 200);
        }
        SuggestFuzziness fuzziness = SuggestFuzziness.of(f -> f
                .fuzziness("AUTO")
                .transpositions(true));

        CompletionSuggester completionSuggester = CompletionSuggester.of(c -> c
                .field("suggest")
                .size(5)
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
        return new ApiResponse<Object>(true, null, completionSuggestion.getEntries(), 200);
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
                            languages.stream().map(FieldValue::of).collect(Collectors.toList())))))
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
                            subcategories.stream().map(FieldValue::of).collect(Collectors.toList())))))
                    ._toQuery());
        }

        if (levels != null && !levels.isEmpty()) {
            filters.add(TermsQuery.of(t -> t
                    .field("level")
                    .terms(TermsQueryField.of(tf -> tf.value(
                            levels.stream().map(FieldValue::of).collect(Collectors.toList())))))
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

    private ApiResponse<Map<String,Object>> buildResponse(List<CourseDocument> courses, long totalHits,
            Map<String, Object> pagination, Map<String, Object> filterCounts) {
        Map<String, Object> response = new HashMap<>();
        response.put("courses", courses);
        response.put("totalResults", totalHits);
        response.put("pagination", pagination);
        response.put("filterCounts", filterCounts);


        return new ApiResponse<>(true, null, response, 200);
    }

}
