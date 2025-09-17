package dev.api.courses.mapper;

import java.math.BigDecimal;

import dev.api.courses.model.redis.CourseRedisEntity;


public class CartCoursesResponse {
    private BigDecimal totalAmount;
    private int totalItems;
    private Iterable<CourseRedisEntity> items;
    
    public CartCoursesResponse(BigDecimal totalAmount, int totalItems, Iterable<CourseRedisEntity> items) {
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.items = items;
    }
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public int getTotalItems() {
        return totalItems;
    }
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
    public Iterable<CourseRedisEntity> getItems() {
        return items;
    }
    public void setItems(Iterable<CourseRedisEntity> items) {
        this.items = items;
    }
    
}
