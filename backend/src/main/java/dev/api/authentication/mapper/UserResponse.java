package dev.api.authentication.mapper;

public class UserResponse  {
    
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String jwt;
    private String role ;
    
    public UserResponse() {
    }

    
    
    public UserResponse(Integer id, String firstName, String lastName, String email, String username, String role , String jwt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.jwt = jwt;
        this.role = role;
    }



    public UserResponse(Integer id, String username , String role , String jwt) {
        this.id = id;
        this.username = username;
        this.jwt = jwt;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
   
    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }



    public String getRole() {
        return role;
    }



    public void setRole(String role) {
        this.role = role;
    }



    @Override
    public String toString() {
        return "[id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
                + ", username=" + username + ", jwt=" + jwt + ", role=" + role + "]";
    }

    
    


    
}
