package dev.api.courses.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class MainCategories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "main_categorie_id")
    private Integer mainCategorieId;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @ManyToMany(mappedBy = "mainCategories")
    private Set<Courses> courses = new HashSet<>();

    
    @OneToMany(mappedBy = "mainCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Subcategories> subcategories = new HashSet<>();

    public Integer getMainCategorieId() {
        return mainCategorieId;
    }
    
    public void setMainCategorieId(Integer mainCategorieId) {
        this.mainCategorieId = mainCategorieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    public Set<Subcategories> getSubcategories() {
        return subcategories;
    }
    
    public void setSubcategories(Set<Subcategories> subcategories) {
        this.subcategories = subcategories;
    }
    
    public Set<Courses> getCourses() {
        return courses;
    }

    public void setCourses(Set<Courses> courses) {
        this.courses = courses;
    }
}