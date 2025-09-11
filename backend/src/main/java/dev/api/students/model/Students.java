package dev.api.students.model;

import java.time.LocalDateTime;

import dev.api.authentication.model.BaseEntity;
import dev.api.authentication.model.Roles;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class Students extends BaseEntity {

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
      
    public Students() {
    }

    public Students(String username, String email,String password_hash, String first_name, String last_name, String profile_picture_url, Roles role) {
        super(username , password_hash , role , false);
        this.first_name = first_name;
        this.last_name = last_name;
        this.profile_picture_url = profile_picture_url;
        this.email = email;
 
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getProfile_picture_url() {
        return profile_picture_url;
    }

    public void setProfile_picture_url(String profile_picture_url) {
        this.profile_picture_url = profile_picture_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }
 
    public LocalDateTime getVerificationCodeExpiresAt() {
        return verificationCodeExpiresAt;
    }
 
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    } 
    
   
    public void setVerificationCodeExpiresAt(LocalDateTime verificationCodeExpiresAt) {
        this.verificationCodeExpiresAt = verificationCodeExpiresAt;
    }

    
}
