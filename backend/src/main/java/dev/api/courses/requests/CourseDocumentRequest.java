package dev.api.courses.requests;
 
public class CourseDocumentRequest {
     
    private String title;
    private String subtitle;
    private String description;
    private String instructor;
    private String category;
    private String subCategory;
    private String level;
    private String language;
    private float price;
    private float averageRating;
    private int numReviews;
    private int numStudents;
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
