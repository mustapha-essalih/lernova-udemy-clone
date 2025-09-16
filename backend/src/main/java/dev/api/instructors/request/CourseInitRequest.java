package dev.api.instructors.request;

import java.math.BigDecimal;
import java.util.List;

import dev.api.common.enums.Languages;
import dev.api.common.enums.Level;
import dev.api.common.enums.Status;


public class CourseInitRequest {

    private String title;
    private String subTitle;
    private String description;
    private BigDecimal price;
    private Boolean isFree;
    private int courseDurationMinutes;
    private Languages language;
    private Status status;
    private Level level;
    private String category;
    private List<SectionsInitRequest> sections;
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSubTitle() {
        return subTitle;
    }
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public Boolean isFree() {
        return isFree;
    }
    public void setFree(Boolean isFree) {
        this.isFree = isFree;
    }
    public int getCourseDurationMinutes() {
        return courseDurationMinutes;
    }
    public void setCourseDurationMinutes(int courseDurationMinutes) {
        this.courseDurationMinutes = courseDurationMinutes;
    }
    public Languages getLanguage() {
        return language;
    }
    public void setLanguage(Languages language) {
        this.language = language;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public Level getLevel() {
        return level;
    }
    public void setLevel(Level level) {
        this.level = level;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public List<SectionsInitRequest> getSections() {
        return sections;
    }
    public void setSections(List<SectionsInitRequest> sections) {
        this.sections = sections;
    }
    @Override
    public String toString() {
        return "CourseInitRequest [title=" + title + ", subTitle=" + subTitle + ", description=" + description
                + ", price=" + price + ", isFree=" + isFree + ", courseDurationMinutes=" + courseDurationMinutes
                + ", language=" + language + ", status=" + status + ", level=" + level + ", category=" + category
                + ", sections=" + sections + "]";
    }


    
    


}