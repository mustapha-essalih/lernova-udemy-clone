package dev.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadErrorDetails {
    private String fileName;
    private String courseTitle;
    private String sectionTitle;
    private String lessonTitle;
    private String resourceTitle;
    private String errorType;
    private String errorMessage;
    private long timestamp;
    private String fileExtension;
    private boolean isRetryable;

    public static UploadErrorDetails fromFileUploadException(dev.api.exception.FileUploadException ex) {
        UploadErrorDetails details = new UploadErrorDetails();
        details.setFileName(ex.getFileName());
        details.setCourseTitle(ex.getCourseTitle());
        details.setSectionTitle(ex.getSectionTitle());
        details.setErrorMessage(ex.getMessage());
        details.setTimestamp(System.currentTimeMillis());
        
        // Determine error type and if it's retryable
        if (ex.getMessage().contains("interrupt") || ex.getMessage().contains("connection") || ex.getMessage().contains("timeout")) {
            details.setErrorType("CONNECTION_ERROR");
            details.setRetryable(true);
        } else if (ex.getMessage().contains("Unsupported file type")) {
            details.setErrorType("INVALID_FILE_TYPE");
            details.setRetryable(false);
        } else if (ex.getMessage().contains("directory not found")) {
            details.setErrorType("DIRECTORY_NOT_FOUND");
            details.setRetryable(false);
        } else {
            details.setErrorType("STORAGE_ERROR");
            details.setRetryable(true);
        }
        
        return details;
    }
}