package dev.api.exception;

public class FileUploadException extends RuntimeException {
    private final String fileName;
    private final String courseTitle;
    private final String sectionTitle;
    
    public FileUploadException(String message, String fileName, String courseTitle, String sectionTitle) {
        super(message);
        this.fileName = fileName;
        this.courseTitle = courseTitle;
        this.sectionTitle = sectionTitle;
    }
    
    public FileUploadException(String message, String fileName, String courseTitle, String sectionTitle, Throwable cause) {
        super(message, cause);
        this.fileName = fileName;
        this.courseTitle = courseTitle;
        this.sectionTitle = sectionTitle;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getCourseTitle() {
        return courseTitle;
    }
    
    public String getSectionTitle() {
        return sectionTitle;
    }
}