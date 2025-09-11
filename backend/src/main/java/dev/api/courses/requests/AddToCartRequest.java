package dev.api.courses.requests;

public class AddToCartRequest {
    
    private Integer courseId;
    private double price;

    public Integer getCourseId() {
        return courseId;
    }
    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

 
    
}
