package dev.api.courses.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;




@Entity
@Table(name = "resources")
@NoArgsConstructor
class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Integer resourceId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "resource_url", nullable = false, columnDefinition = "TEXT")
    private String resourceUrl;

    @Column(name = "is_preview")
    private Boolean isPreview;

    @Column(name = "file_type", length = 50)
    private String fileType;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;


    public Integer getResourceId() {
        return resourceId;
    }


    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getResourceUrl() {
        return resourceUrl;
    }


    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }


    public Boolean getIsPreview() {
        return isPreview;
    }


    public void setIsPreview(Boolean isPreview) {
        this.isPreview = isPreview;
    }


    public String getFileType() {
        return fileType;
    }


    public void setFileType(String fileType) {
        this.fileType = fileType;
    }


    public Lesson getLesson() {
        return lesson;
    }


    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }



    
}