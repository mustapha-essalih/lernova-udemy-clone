package dev.api.courses.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Sections {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sectionId;

    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Courses course;

    @Column(nullable = false)
    private String title;

    
    
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Videos> videos = new HashSet<>();

    
    
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SectionResources> sectionResources = new HashSet<>();



    public Integer getSectionId() {
        return sectionId;
    }



    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

 

    public String getTitle() {
        return title;
    }



    public void setTitle(String title) {
        this.title = title;
    }



    public Set<Videos> getVideos() {
        return videos;
    }



    public void setVideos(Set<Videos> videos) {
        this.videos = videos;
    }



    public Set<SectionResources> getSectionResources() {
        return sectionResources;
    }



    public void setSectionResources(Set<SectionResources> sectionResources) {
        this.sectionResources = sectionResources;
    }



    public Courses getCourse() {
        return course;
    }



    public void setCourse(Courses course) {
        this.course = course;
    }


    

}
