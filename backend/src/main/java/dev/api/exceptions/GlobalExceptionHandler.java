package dev.api.exceptions;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.api.common.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> runTimeException(RuntimeException e) {
        if (e instanceof AccessDeniedException) {
            ApiResponse<Object> response = new ApiResponse<>(
                false, 
                "You don't have permission to access this resource", 
                null
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        System.out.println("RuntimeException");
        System.out.println(e.getLocalizedMessage());
        
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            "Internal Server Error",
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value = InternalServerError.class)
    public ResponseEntity<ApiResponse<Object>> internalServerError(InternalServerError e) {
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            e.getMessage() != null ? e.getMessage() : "Internal Server Error",
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParams(MissingServletRequestParameterException e) {
        String message = "Required request parameter '" + e.getParameterName() + "' is missing";
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            message,
            null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException e) {
        StringBuilder message = new StringBuilder();
        message.append(e.getContentType()).append(" media type is not supported. Supported media types are ");
        e.getSupportedMediaTypes().forEach(t -> message.append(t).append(", "));
        String errorMessage = message.substring(0, message.length() - 2); // remove trailing comma and space
        
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            errorMessage,
            null
        );
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> resourceNotFound(ResourceNotFoundException e) {
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            e.getMessage() != null ? e.getMessage() : "Resource Not Found",
            null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = DisabledException.class)
    public ResponseEntity<ApiResponse<Object>> disabledUser(DisabledException e) {
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            "Should activate your email, check your email",
            null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
            false,
            "Validation failed",
            errorMap
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}