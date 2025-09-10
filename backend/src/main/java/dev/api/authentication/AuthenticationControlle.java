package dev.api.authentication;

import dev.api.authentication.request.EmailRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.api.authentication.request.EmailRequest;
import dev.api.authentication.request.LoginRequest;
import dev.api.authentication.request.RegistrationRequest;
import dev.api.common.EmailService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationControlle {

    private AuthenticationService authenticationService;
    private EmailService emailService;

    @PostMapping("/students")
    public ResponseEntity<?> registerNewStudent(@RequestBody @Valid RegistrationRequest request) {

        return authenticationService.registerNewStudent(request);
    }

    @PostMapping("/instructors")
    public ResponseEntity<?> registerNewInstructor(@RequestBody @Valid RegistrationRequest request) {

        return authenticationService.registerNewInstructor(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return authenticationService.login(request, response);
    }

    @GetMapping("/email-verification")
    public ResponseEntity<String> emailVerification(@RequestParam String token) {
        return emailService.emailVerification(token);
    }

    @PostMapping("/resend-email-verification")
    ResponseEntity<String> resendEmailVerification(@RequestBody EmailRequest email) {
        return authenticationService.resendEmailVerification(email.getEmail());
    }

    
    @PostMapping("/forget-password")  
    public ResponseEntity<String> forgetPassword(@RequestBody EmailRequest email){
        return null;
    }
    


}
