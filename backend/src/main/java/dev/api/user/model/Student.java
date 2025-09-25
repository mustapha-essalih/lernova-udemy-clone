package dev.api.user.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.api.common.BaseEntity;
import dev.api.common.enums.Roles;
import dev.api.courses.model.Course;
import dev.api.courses.model.Order;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Table(name="students")
@Entity
public class Student extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, length = 100)
    private String first_name;

    @Column(nullable = false, length = 100)
    private String last_name;

    @Column(columnDefinition = "TEXT")
    private String profile_picture_url;
 
    private String verificationCode;
    
    @Column(name = "verification_expiration")
    private LocalDateTime verificationCodeExpiresAt;

    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @ManyToMany(mappedBy = "students")
    private Set<Course> courses = new HashSet<>();
    

    public Student(String username, String email,String password_hash, String first_name, String last_name, String profile_picture_url, Roles role) {
        super(username , password_hash , role , false);
        this.first_name = first_name;
        this.last_name = last_name;
        this.profile_picture_url = profile_picture_url;
        this.email = email;
    }
}
