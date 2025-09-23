package dev.api.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import dev.api.controller.dto.FileMetadata;
import dev.api.exception.BadRequestException;
import dev.api.exception.FileUploadException;
import dev.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    @Value("${app.upload-directory}")
    private String uploadDirectory;

    // Allowed file types for Udemy platform
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        // Documents
        ".pdf", ".doc", ".docx", ".txt", ".rtf",
        // Archives (for resources)
        ".zip", ".rar", ".7z",
        // Images (for thumbnails, diagrams, etc.)
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg",
        // Videos (main course content)
        ".mp4", ".avi", ".mov", ".wmv", ".flv", ".mkv", ".webm",
        // Audio (for audio lessons)
        ".mp3", ".wav", ".aac", ".ogg", ".m4a",
        // Presentations
        ".ppt", ".pptx",
        // Spreadsheets
        ".xls", ".xlsx", ".csv",
        // Links/URLs
        ".url", ".link"
    );

    public Mono<Void> uploadCourseResource(Mono<FilePart> filePartMono, FileMetadata metadata) {
        return filePartMono
                .flatMap(filePart -> {
                    // Validate file type first
                    String originalFileName = filePart.filename();
                    String extension = validateFileType(originalFileName);
                    
                    log.info("Processing file: {} with extension: {}", originalFileName, extension);
                    
                    return validateAndPreparePath(metadata, extension, originalFileName)
                            .flatMap(path -> writeFileWithProgress(filePart, path, metadata))
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .onErrorMap(throwable -> {
                    if (throwable instanceof RuntimeException) {
                        return throwable;
                    }
                    return new FileUploadException(
                        "Unexpected error during upload: " + throwable.getMessage(),
                        metadata.getResourceTitle(),
                        metadata.getCourseTitile(),
                        metadata.getSectionTitile(),
                        throwable
                    );
                });
    }

    private String validateFileType(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new BadRequestException("File name is required");
        }

        // Extract extension
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            throw new BadRequestException("File must have a valid extension");
        }

        String extension = fileName.substring(lastDotIndex).toLowerCase();
        
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(
                "Unsupported file type: " + extension + 
                ". Please upload only educational content files (PDF, DOC, images, videos, audio, presentations, etc.)"
            );
        }

        return extension;
    }

    private Mono<Path> validateAndPreparePath(FileMetadata metadata, String extension, String originalFileName) {
        return Mono.fromCallable(() -> {
            // Build path: uploadDirectory/courseTitle/sectionTitle/lessonTitle/resourceTitle
            Path coursePath = Paths.get(uploadDirectory, sanitizeTitle(metadata.getCourseTitile()));
            Path sectionPath = coursePath.resolve(sanitizeTitle(metadata.getSectionTitile()));
            Path lessonPath = sectionPath.resolve(sanitizeTitle(metadata.getLessonTitle()));
            
 
            // Check if course, section, and lesson directories exist
            
            if (!Files.exists(lessonPath)) {
                throw new ResourceNotFoundException(
                    "Course, section, or lesson directory not found: " + lessonPath + 
                    ". Please ensure the course '" + metadata.getCourseTitile() + 
                    "', section '" + metadata.getSectionTitile() + 
                    "', and lesson '" + metadata.getLessonTitle() + "' exist."
                );
            }
            // Create full file path with resource title and detected extension
            String baseFileName = metadata.getResourceTitle() != null ? 
                sanitizeTitle(metadata.getResourceTitle()) : 
                sanitizeTitle(originalFileName.substring(0, originalFileName.lastIndexOf('.')));
            String fileName = baseFileName + extension;
            
            Path filePath = lessonPath.resolve(fileName);
            return filePath;
        });
    }

    private Mono<Void> writeFileWithProgress(FilePart filePart, Path path, FileMetadata metadata) {
        return filePart.transferTo(path)
                .doOnSuccess(unused -> {
                    try {
                        long fileSize = Files.size(path);
                        log.info("Successfully uploaded '{}' ({} bytes) to {}", 
                                path.getFileName().toString(), fileSize, path.getParent().toString());
                    } catch (Exception e) {
                        log.warn("Could not get file size for: {}", path);
                    }
                })
                .onErrorMap(throwable -> {
                    // Handle specific upload errors
                    if (throwable.getMessage() != null && 
                        (throwable.getMessage().contains("connection") || 
                         throwable.getMessage().contains("interrupt") ||
                         throwable.getMessage().contains("timeout"))) {
                        
                        return new FileUploadException(
                            "Upload interrupted or connection lost: " + throwable.getMessage(),
                            metadata.getResourceTitle() != null ? metadata.getResourceTitle() : filePart.filename(),
                            metadata.getCourseTitile(),
                            metadata.getSectionTitile(),
                            throwable
                        );
                    }
                    
                    return new FileUploadException(
                        "Failed to write file to storage: " + throwable.getMessage(),
                        metadata.getResourceTitle() != null ? metadata.getResourceTitle() : filePart.filename(),
                        metadata.getCourseTitile(),
                        metadata.getSectionTitile(),
                        throwable
                    );
                });
    }

    private String sanitizeTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "untitled";
        }
        // Replace spaces with underscores and remove special characters
        return title.trim()
                   .replaceAll("\\s+", "_")
                   .replaceAll("[^a-zA-Z0-9._-]", "")
                   .toLowerCase();
    }
}
