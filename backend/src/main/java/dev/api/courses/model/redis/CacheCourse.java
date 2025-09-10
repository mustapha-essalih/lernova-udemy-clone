package dev.api.courses.model.redis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import dev.api.courses.model.Languages;
import dev.api.courses.model.Level;
import dev.api.courses.model.Status;
import dev.api.instructors.request.SectionsInitRequest;

@RedisHash("courses")
public class CacheCourse {

    @Id
    private String id;
    private String title;
    private String subTitle;
    private String description;
    private double price;
    private boolean isFree;
    private int courseDurationMinutes;
    private Languages language;
    private Status status;
    private Level level;
    private String category;
    private List<String> sectionIds = new ArrayList<>(); // Stores only section IDs


    public CacheCourse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean isFree) {
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

    public List<String> getSectionIds() {
        return sectionIds;
    }

    public void setSectionIds(List<String> sectionIds) {
        this.sectionIds = sectionIds;
    }


}
