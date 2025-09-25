package dev.api.service;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import dev.api.entity.LessonRedisEntity;
import dev.api.entity.ResourceRedisEntity;
import dev.api.exception.ResourceNotFoundException;
import dev.api.repository.LessonRedisRepository;
import dev.api.repository.ResourceRedisRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class ManagerService {

    private  ResourceRedisRepository resourceRedisRepository;
    private LessonRedisRepository lessonRedisRepository;


     public Mono<Void> getResourceFile(@PathVariable String resourceId, ServerHttpResponse response) {
        return Mono.fromCallable(() -> {
            // Get resource entity from Redis
            ResourceRedisEntity resource = resourceRedisRepository.findById(resourceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + resourceId));
            
            // Get file path from resource entity
            String filePath = resource.getResourcePath();
            if (filePath == null || filePath.trim().isEmpty()) {
                throw new ResourceNotFoundException("File path not found for resource ID: " + resourceId);
            }
            
            try {
                return resolveActualFilePath(filePath);
            } catch (IOException e) {
                throw new ResourceNotFoundException("Failed to resolve file path: " + filePath + " - " + e.getMessage());
            }
        })
        .flatMap(actualFilePath -> {
            // Set headers for inline display in browser
            String fileName = actualFilePath.getFileName().toString();
            String contentType = determineContentType(fileName);
            
            response.getHeaders().add(HttpHeaders.CONTENT_TYPE, contentType);
            response.getHeaders().add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"");
            response.getHeaders().add(HttpHeaders.CACHE_CONTROL, "public, max-age=3600"); // Cache for 1 hour
            response.getHeaders().add("X-Content-Type-Options", "nosniff");
            
            Flux<DataBuffer> fileStream = DataBufferUtils.read(actualFilePath, new DefaultDataBufferFactory(), 8192);
 
            return response.writeWith(fileStream);
        })
        .onErrorMap(throwable -> {
            if (throwable instanceof ResourceNotFoundException) {
                return throwable;
            }
            return new ResourceNotFoundException("Failed to download resource file: " + throwable.getMessage());
        });
    }







     public Mono<Void> getLessonFile(String lessonId, ServerHttpResponse response) {
         return Mono.fromCallable(() -> {
            // Get lesson entity from Redis
            LessonRedisEntity lesson = lessonRedisRepository.findById(lessonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with ID: " + lessonId));
            
                    System.out.println("-------------------- " + lesson);
            // Get file path from lesson entity
            String filePath = lesson.getLessonPath();
            
            if (filePath == null || filePath.trim().isEmpty()) {
                throw new ResourceNotFoundException("File path not found for lesson ID: " + lessonId);
            }
            
            try {
                return resolveActualFilePath(filePath);
            } catch (IOException e) {
                throw new ResourceNotFoundException("Failed to resolve file path: " + filePath + " - " + e.getMessage());
            }
        })
        .flatMap(actualFilePath -> {
            // Set headers for inline display in browser
            String fileName = actualFilePath.getFileName().toString();
            String contentType = determineContentType(fileName);
            
            response.getHeaders().add(HttpHeaders.CONTENT_TYPE, contentType);
            response.getHeaders().add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"");
            response.getHeaders().add(HttpHeaders.CACHE_CONTROL, "public, max-age=3600"); // Cache for 1 hour
            response.getHeaders().add("X-Content-Type-Options", "nosniff");
            
            // Stream the file
            Flux<DataBuffer> fileStream = DataBufferUtils.read(actualFilePath, new DefaultDataBufferFactory(), 8192);
            return response.writeWith(fileStream);
        })
        .onErrorMap(throwable -> {
            if (throwable instanceof ResourceNotFoundException) {
                return throwable;
            }
            return new ResourceNotFoundException("Failed to download lesson file: " + throwable.getMessage());
        });
     }

    /**
     * Helper method to resolve the actual file path from a directory or file path
     * If the path is a directory, it finds the first regular file inside
     * If the path is already a file, it returns it directly
     */
    private Path resolveActualFilePath(String pathString) throws IOException {
        Path path = Paths.get(pathString);
        
        if (Files.isDirectory(path)) {
            try (Stream<Path> files = Files.list(path)) {
                return files
                    .filter(Files::isRegularFile)
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No file found in directory: " + pathString));
            }
        } else if (Files.isRegularFile(path)) {
            return path;
        } else {
            throw new ResourceNotFoundException("Path does not exist or is not accessible: " + pathString);
        }
    }

    /**
     * Determines the appropriate Content-Type based on file extension
     */
    private String determineContentType(String fileName) {
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        
        switch (extension) {
            // Images
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG_VALUE;
            case "png":
                return MediaType.IMAGE_PNG_VALUE;
            case "gif":
                return MediaType.IMAGE_GIF_VALUE;
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";
            case "bmp":
                return "image/bmp";
            
            // Videos
            case "mp4":
                return "video/mp4";
            case "webm":
                return "video/webm";
            case "avi":
                return "video/x-msvideo";
            case "mov":
                return "video/quicktime";
            case "mkv":
                return "video/x-matroska";
            
            // Audio
            case "mp3":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            case "ogg":
                return "audio/ogg";
            case "m4a":
                return "audio/mp4";
            case "aac":
                return "audio/aac";
            
            // Documents
            case "pdf":
                return MediaType.APPLICATION_PDF_VALUE;
            case "txt":
                return MediaType.TEXT_PLAIN_VALUE;
            case "html":
            case "htm":
                return MediaType.TEXT_HTML_VALUE;
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "json":
                return MediaType.APPLICATION_JSON_VALUE;
            case "xml":
                return MediaType.APPLICATION_XML_VALUE;
            
            // Office documents
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            
            // Archives
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            case "7z":
                return "application/x-7z-compressed";
            
            // Default
            default:
                return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }






 

}
