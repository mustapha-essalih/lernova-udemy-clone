package dev.api.courses.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Resources {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resourceId;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Sections section;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String resourceUrl; 

    @Column(length = 50)
    private String fileType; 


    @Column(nullable = false)
    private Boolean isPreview;



    public Sections getSection() {
        return section;
    }


    public void setSection(Sections section) {
        this.section = section;
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


    public String getFileType() {
        return fileType;
    }


    public void setFileType(String fileType) {
        this.fileType = fileType;
    }


    public Boolean getIsPreview() {
        return isPreview;
    }


    public void setIsPreview(Boolean isPreview) {
        this.isPreview = isPreview;
    }


    public Integer getResourceId() {
        return resourceId;
    }


    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }


    
    
}
