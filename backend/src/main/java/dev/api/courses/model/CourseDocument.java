package dev.api.courses.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

 
@Document(indexName = "courses")
public class CourseDocument {

    
    @Id
    private String courseId;

    
    @Field(type = FieldType.Text, analyzer = "english") 
    private String title;

    
    @Field(type = FieldType.Text, analyzer = "english")
    private String subtitle;

    
    @Field(type = FieldType.Text, analyzer = "english")
    private String description;

    @Field(type = FieldType.Text) 
    private String instructorName;
    
    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String subCategory;

    @Field(type = FieldType.Keyword)
    private String level;

    @Field(type = FieldType.Keyword)
    private String language;
    
    @Field(type = FieldType.Float)
    private float price;

    @Field(type = FieldType.Float)
    private float averageRating;

    @Field(type = FieldType.Integer)
    private int numReviews;

    @Field(type = FieldType.Integer)
    private int numStudents;

    
    // @Field(type = FieldType.Date, format = FieldArrayType.NONE, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    // private Instant lastUpdated; 


    
    public CourseDocument() {
    }


    public CourseDocument(String title, String subtitle, String description, String instructorName,
            String category, String subCategory, String level, String language, float price, float averageRating,
            int numReviews, int numStudents) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.instructorName = instructorName;
        this.category = category;
        this.subCategory = subCategory;
        this.level = level;
        this.language = language;
        this.price = price;
        this.averageRating = averageRating;
        this.numReviews = numReviews;
        this.numStudents = numStudents;
    }


    public String getCourseId() {
        return courseId;
    }


    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getSubtitle() {
        return subtitle;
    }


    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getInstructorName() {
        return instructorName;
    }


    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }


    public String getCategory() {
        return category;
    }


    public void setCategory(String category) {
        this.category = category;
    }


    public String getSubCategory() {
        return subCategory;
    }


    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }


    public String getLevel() {
        return level;
    }


    public void setLevel(String level) {
        this.level = level;
    }


    public String getLanguage() {
        return language;
    }


    public void setLanguage(String language) {
        this.language = language;
    }


    public float getPrice() {
        return price;
    }


    public void setPrice(float price) {
        this.price = price;
    }


    public float getAverageRating() {
        return averageRating;
    }


    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }


    public int getNumReviews() {
        return numReviews;
    }


    public void setNumReviews(int numReviews) {
        this.numReviews = numReviews;
    }


    public int getNumStudents() {
        return numStudents;
    }


    public void setNumStudents(int numStudents) {
        this.numStudents = numStudents;
    }


    

}