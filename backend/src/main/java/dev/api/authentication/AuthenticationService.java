package dev.api.authentication;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.api.authentication.dto.LoginRequest;
import dev.api.authentication.dto.RegistrationRequest;
import dev.api.common.ApiResponse;
import dev.api.common.EmailService;
import dev.api.common.GeneraleService;
import dev.api.common.enums.Roles;
import dev.api.security.JwtService;
import dev.api.user.model.Instructors;
import dev.api.user.model.Student;
import dev.api.user.repository.InstructorsRepository;
import dev.api.user.repository.StudentsRepository;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthenticationService {

    private StudentsRepository studentsRepository;
    private InstructorsRepository instructorsRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private EmailService emailService;
    private GeneraleService generaleService;

    @Value("${site.base.url.http}")
    private String urlOfRequest;

    @Value("${token.expirationms}")
    private long jwtExpiration;

    public AuthenticationService(StudentsRepository studentsRepository, InstructorsRepository instructorsRepository,
            GeneraleService generaleService,
            PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService,
            EmailService emailService) {
        this.studentsRepository = studentsRepository;
        this.instructorsRepository = instructorsRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.generaleService = generaleService;
    }

    public ResponseEntity<ApiResponse<?>> registerNewStudent(RegistrationRequest request) {

        Map<String, String> user = new HashMap<>();
        if (studentsRepository.findByUsername(request.getUsername()).isPresent() ||
                studentsRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Username or email already exists", null));
        } else {

            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Passwords do not match", null));
            }

            String passwordHashed = passwordEncoder.encode(request.getPassword());

            Student student = new Student(request.getUsername(), request.getEmail(), passwordHashed,
                    request.getFirstName(),
                    request.getLastName(), null, Roles.STUDENT);

            sendEmailVerification(student, null);
            user.put("username", student.getUsername());
            user.put("email", student.getEmail());

        }
        return ResponseEntity.status(201).body(new ApiResponse<>(true, "Student registered successfully. Please check your email for verification.", user));
    }

    public ResponseEntity<ApiResponse<?>> registerNewInstructor(RegistrationRequest request) {

        Map<String, String> user = new HashMap<>();

        if (instructorsRepository.findByUsername(request.getUsername()).isPresent() ||
                instructorsRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Username or email already exists", null));
        } else {

            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Passwords do not match", null));
            }

            String passwordHashed = passwordEncoder.encode(request.getPassword());

            Instructors instructor = new Instructors(request.getUsername(), request.getEmail(), passwordHashed,
                    request.getFirstName(),
                    request.getLastName(), null, Roles.INSTRUCTOR);

            sendEmailVerification(null, instructor);
            user.put("username", instructor.getUsername());
            user.put("email", instructor.getEmail());
        }

        return ResponseEntity.status(201).body(new ApiResponse<>(true, "Instructor registered successfully. Please check your email for verification.", user));
    }

    public ResponseEntity<ApiResponse<?>> login(LoginRequest request, HttpServletResponse response) {

        Authentication authenticationRequest = UsernamePasswordAuthenticationToken
                .unauthenticated(request.getUsername(), request.getPassword());

        try {
            Authentication authenticatedUser = this.authenticationManager.authenticate(authenticationRequest);
            String jwt = jwtService.generateToken(authenticatedUser.getName());

            ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true) // HttpOnly cookies prevent JavaScript from accessing or modifying them, letting
                                    // only the browser automatically send them to the server.
                    // .secure(true)
                    .path("/")
                    .maxAge(Duration.ofMinutes(jwtExpiration))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "Login successful", generaleService.getUserInfos(authenticationRequest.getName(), jwt)));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "Invalid username or password", null));
        } catch (Exception e) {
            if (e.getMessage().equals("User is disabled")) {
                return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(new ApiResponse<>(false, "Please verify your email first", null));
            }
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "An unexpected error occurred during login", null));
        }
    }

    public ResponseEntity<ApiResponse<String>> resendEmailVerification(String email) {

        Student student = studentsRepository.findByEmail(email).orElse(null);
        if (student != null) {
            if (student.isEnabled())
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "This account has already been verified, please, login.", null));

            sendEmailVerification(student, null);
            return ResponseEntity.ok(new ApiResponse<>(true, "Please, check your email to complete your registration", null));
        }

        Instructors instructor = instructorsRepository.findByEmail(email).orElse(null);
        if (instructor != null) {
            if (instructor.isEnabled())
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "This account has already been verified, please, login.", null));

            sendEmailVerification(null, instructor);
            return ResponseEntity.ok(new ApiResponse<>(true, "Please, check your email to complete your registration", null));
        }

        return ResponseEntity.status(HttpStatus.SC_GONE).body(new ApiResponse<>(false, "User not found", null));
    }

    private void sendEmailVerification(Student student, Instructors instructor) {

        String generateVerificationToken = UUID.randomUUID().toString();
        if (student != null) {
            student.setVerificationCode(generateVerificationToken);
            student.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
            studentsRepository.save(student);
            String url = urlOfRequest + "/verify?token=" + generateVerificationToken;
            emailService.sendEmailVerification(student.getUsername(), student.getEmail(), url);
        } else {
            instructor.setVerificationCode(generateVerificationToken);
            instructor.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));

            instructorsRepository.save(instructor);

            String url = urlOfRequest + generateVerificationToken;

            emailService.sendEmailVerification(instructor.getUsername(), instructor.getEmail(), url);
        }

    }

}
