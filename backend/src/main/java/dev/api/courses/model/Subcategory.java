package dev.api.courses.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@Table(name = "subcategories")
@NoArgsConstructor
@Entity
public class Subcategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer subcategoryId; 

    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "main_category_id", nullable = false) 
    private MainCategory mainCategory; 

    @Column(nullable = false, length = 100) 
    private String name;
    
    
}