package dev.api.authentication;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.api.authentication.dto.EmailRequest;
import dev.api.authentication.dto.LoginRequest;
import dev.api.authentication.dto.RegistrationRequest;
import dev.api.common.ApiResponse;
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
    public ResponseEntity<ApiResponse<?>> registerNewStudent(@RequestBody @Valid RegistrationRequest request) {

        return authenticationService.registerNewStudent(request);
    }

    @PostMapping("/instructors")
    public ResponseEntity<ApiResponse<?>> registerNewInstructor(@RequestBody @Valid RegistrationRequest request) {

        return authenticationService.registerNewInstructor(request);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return authenticationService.login(request, response);
    }

    @GetMapping("/email-verification")
    public ResponseEntity<ApiResponse<String>> emailVerification(@RequestParam String token) {
        return emailService.emailVerification(token);
    }

    @PostMapping("/resend-email-verification")
    ResponseEntity<ApiResponse<String>> resendEmailVerification(@RequestBody EmailRequest email) {
        return authenticationService.resendEmailVerification(email.getEmail());
    }

    
    @PostMapping("/forget-password")  
    public ResponseEntity<ApiResponse<String>> forgetPassword(@RequestBody EmailRequest email){
        return ResponseEntity.ok(new ApiResponse<>(true, "Password reset functionality not implemented yet", null));
    }
    

}
