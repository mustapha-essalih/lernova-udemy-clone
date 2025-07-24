package dev.api.exceptions;




import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
 
@RestControllerAdvice
public class GlobalExceptionHandler { 
 
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<?> runTimeException(RuntimeException e) {
       
        if (e instanceof AccessDeniedException) 
        {
            return ResponseEntity.status(403).body("you don't have permission to access this resource");
        }
       
        System.out.println("RuntimeException");
        System.out.println(e.getLocalizedMessage());
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", 500);
        errorDetails.put("error", "Internal Server Error");
        errorDetails.put("message", e.getLocalizedMessage());
        return ResponseEntity.status(500).body(errorDetails);
    }


    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<?> courceException(ResourceNotFoundException e) 
    {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", 404);
        errorDetails.put("error", "Resource Not Found");
        errorDetails.put("message", e.getMessage());
        return ResponseEntity.status(404).body(errorDetails);
    }

    @ExceptionHandler(value = DisabledException.class)
    public ResponseEntity<?> DisabledUser(DisabledException e) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", 403);
        errorDetails.put("error", "Forbidden");
        errorDetails.put("message", "should activate your email, check your email");
        return ResponseEntity.status(403).body(errorDetails);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.status(400).body(errorMap);
    }

}
