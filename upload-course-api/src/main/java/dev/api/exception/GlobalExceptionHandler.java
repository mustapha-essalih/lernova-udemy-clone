package dev.api.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import org.springframework.web.server.ServerWebInputException;

import dev.api.dto.ApiResponse;
import dev.api.dto.UploadErrorDetails;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> runTimeException(RuntimeException e) {
        log.error("RuntimeException occurred: {}", e.getLocalizedMessage(), e);
        
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            "Internal Server Error",
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Invalid request body: {}", e.getMessage());
        
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            "Invalid request body",
            null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> businessException(BusinessException e) {
        log.warn("Business exception: {}", e.getMessage());
        
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            e.getMessage(),
            null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(value = InternalServerError.class)
    public ResponseEntity<ApiResponse<Object>> internalServerError(InternalServerError e) {
        log.error("Internal server error: {}", e.getMessage(), e);
        
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            e.getMessage() != null ? e.getMessage() : "Internal Server Error",
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ApiResponse<Object>> handleServerWebInputException(ServerWebInputException e) {
        log.warn("Invalid request: {}", e.getMessage());
        
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            "Invalid request parameters",
            null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnsupportedMediaType(UnsupportedMediaTypeStatusException e) {
        log.warn("Unsupported media type: {}", e.getMessage());
        
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            "Unsupported media type",
            null
        );
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> resourceNotFound(ResourceNotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            e.getMessage() != null ? e.getMessage() : "Resource Not Found",
            null
        );
        return ResponseEntity.status(HttpStatus.GONE).body(response);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException e) {
        log.warn("Bad request: {}", e.getMessage());
        
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            e.getMessage(),
            null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        
        log.warn("Validation failed: {}", errorMap);
        
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
            false,
            "Validation failed",
            errorMap
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = FileUploadException.class)
    public ResponseEntity<ApiResponse<UploadErrorDetails>> handleFileUploadException(FileUploadException e) {
        log.error("File upload failed: {}", e.getMessage(), e);
        
        UploadErrorDetails errorDetails = UploadErrorDetails.fromFileUploadException(e);
        
        ApiResponse<UploadErrorDetails> response = new ApiResponse<>(
            false,
            "File upload failed",
            errorDetails
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}