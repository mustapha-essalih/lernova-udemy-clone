package dev.api.courses.responses;

import java.math.BigDecimal;

import dev.api.courses.model.redis.CacheCourse;

public class CartCoursesResponse {
    private BigDecimal totalAmount;
    private int totalItems;
    private Iterable<CacheCourse> items;
    
    
    

    public CartCoursesResponse(BigDecimal totalAmount, int totalItems, Iterable<CacheCourse> items) {
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
    public Iterable<CacheCourse> getItems() {
        return items;
    }
    public void setItems(Iterable<CacheCourse> items) {
        this.items = items;
    }


    
    
}
