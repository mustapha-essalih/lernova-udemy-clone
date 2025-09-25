package dev.api.courses.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.api.common.ApiResponse;
import dev.api.courses.model.CourseDocument;
import dev.api.courses.service.SearchService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private SearchService searchService;

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<Map<String,Object>>> searchCourses(@RequestParam String q,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) List<String> languages,
            @RequestParam(required = false) List<String> durations,
            @RequestParam(required = false) List<String> subcategories,
            @RequestParam(required = false) List<String> levels,
            @RequestParam(required = false) List<String> priceTypes,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        ApiResponse<Map<String,Object>> searchCourses = this.searchService.searchCourses(q, rating, languages, durations,
                subcategories, levels, priceTypes, page, size);

        return ResponseEntity.ok(searchCourses);
    }

    @GetMapping("/suggest")
    public ResponseEntity< ApiResponse<List<String>>> getSuggestions(@RequestParam String prefix) {
        return ResponseEntity.ok(searchService.getSuggestions(prefix));
    }

    @GetMapping("/instructors/suggest")
    public ResponseEntity<ApiResponse<List<CourseDocument>>> suggestInstructors(@RequestParam String prefix) {
        return ResponseEntity.ok(searchService.suggestInstructors(prefix));
    }

    @GetMapping("/titles/suggest")
    public ResponseEntity<ApiResponse<Object>> suggestCourseTitles(@RequestParam String prefix) {
        System.out.println(prefix);
        return ResponseEntity.ok(searchService.suggestCourseTitles(prefix));
    }


}