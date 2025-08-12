package dev.api.courses.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.core.suggest.Completion;


@Setting(settingPath = "/elasticsearch/course-settings.json")
@Document(indexName = "courses")
public class CourseDocument {

    @Id
    private String courseId;

    @MultiField(mainField = @Field(type = FieldType.Text), otherFields = {
            @InnerField(suffix = "trigram", type = FieldType.Text, analyzer = "trigram"),
            @InnerField(suffix = "reverse", type = FieldType.Text, analyzer = "reverse")
    })
    private String title;


    @MultiField(mainField = @Field(type = FieldType.Text), otherFields = {
            @InnerField(suffix = "trigram", type = FieldType.Text, analyzer = "trigram")
    })
    private String subtitle;


    @MultiField(mainField = @Field(type = FieldType.Text), otherFields = {
            @InnerField(suffix = "trigram", type = FieldType.Text, analyzer = "trigram")
    })
    private String description;


    @MultiField(mainField = @Field(type = FieldType.Text), otherFields = {
            @InnerField(suffix = "trigram", type = FieldType.Text, analyzer = "trigram")
    })
    private String instructor;

    @CompletionField
    private Completion suggest;


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





    public CourseDocument() {
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

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public Completion getSuggest() {
        return suggest;
    }

    public void setSuggest(Completion suggest) {
        this.suggest = suggest;
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