package dev.api.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import dev.api.controller.dto.FileMetadata;
import dev.api.dto.ApiResponse;
import dev.api.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/instructor")
@RestController
public class UploadController {

    @Value("${app.upload-directory}")
    private String uploadDirectory;

    private final UploadService uploadService;

    @PostMapping(value = "/courses")
    public Mono<ApiResponse<Object>> uploadLargeFile(
            @RequestPart("file") Mono<FilePart> filePart,
            @Valid @RequestPart("metadata") FileMetadata metadata) {
        
        log.info("Starting upload for course: {}, section: {}, lesson: {}, resource: {}", 
                metadata.getCourseTitile(), metadata.getSectionTitile(), 
                metadata.getLessonTitle(), metadata.getResourceTitle());
        
        return uploadService.uploadCourseResource(filePart, metadata)
                .then(Mono.just(new ApiResponse<>(true, "File uploaded successfully", null)))
                .doOnSuccess(response -> log.info("Upload completed successfully"))
                .doOnError(error -> log.error("Upload failed: {}", error.getMessage()));
    }
}