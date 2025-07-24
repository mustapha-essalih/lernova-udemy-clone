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
public class Subcategories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer subcategoryId; 

    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "main_category_id", nullable = false) 
    private MainCategories mainCategory; 

    @Column(nullable = false, length = 100) 
    private String name;

    public Integer getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(Integer subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public MainCategories getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(MainCategories mainCategory) {
        this.mainCategory = mainCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

     
    
    
    
    
    
}