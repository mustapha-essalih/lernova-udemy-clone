package dev.api.courses.responses;

import java.util.ArrayList;
import java.util.List;

public class CacheCourseResponse {
    
    private String courseId;
    private List<SectionsResponse> sections = new ArrayList<>();
    private List<FilesResponses> files = new ArrayList<>();
    
    public String getCourseId() {
        return courseId;
    }
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    public List<SectionsResponse> getSections() {
        return sections;
    }
    public void setSections(List<SectionsResponse> sections) {
        this.sections = sections;
    }
    public List<FilesResponses> getFiles() {
        return files;
    }
    public void setFiles(List<FilesResponses> files) {
        this.files = files;
    }


    

}
