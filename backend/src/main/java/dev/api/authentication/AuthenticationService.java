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

import dev.api.authentication.model.Roles;
import dev.api.authentication.request.LoginRequest;
import dev.api.authentication.request.RegistrationRequest;
import dev.api.common.EmailService;
import dev.api.common.GeneraleService;
import dev.api.instructors.model.Instructors;
import dev.api.instructors.repository.InstructorsRepository;
import dev.api.security.JwtService;
import dev.api.students.model.Students;
import dev.api.students.repository.StudentsRepository;
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

    public ResponseEntity<?> registerNewStudent(RegistrationRequest request) {

        Map<String, String> user = new HashMap<>();
        if (studentsRepository.findByUsername(request.getUsername()).isPresent() ||
                studentsRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Username or email already exists");
        } else {

            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Passwords do not match");
            }

            String passwordHashed = passwordEncoder.encode(request.getPassword());

            Students student = new Students(request.getUsername(), request.getEmail(), passwordHashed,
                    request.getFirstName(),
                    request.getLastName(), null, Roles.STUDENT);

            sendEmailVerification(student, null);
            user.put("username", student.getUsername());
            user.put("email", student.getEmail());

        }
        return ResponseEntity.status(201).body(user);
    }

    public ResponseEntity<?> registerNewInstructor(RegistrationRequest request) {

        Map<String, String> user = new HashMap<>();

        if (instructorsRepository.findByUsername(request.getUsername()).isPresent() ||
                instructorsRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Username or email already exists");
        } else {

            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Passwords do not match");
            }

            String passwordHashed = passwordEncoder.encode(request.getPassword());

            Instructors instructor = new Instructors(request.getUsername(), request.getEmail(), passwordHashed,
                    request.getFirstName(),
                    request.getLastName(), null, Roles.INSTRUCTOR);

            sendEmailVerification(null, instructor);
            user.put("username", instructor.getUsername());
            user.put("email", instructor.getEmail());
        }

        return ResponseEntity.status(201).body(user);
    }

    public ResponseEntity<?> login(LoginRequest request, HttpServletResponse response) {

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
                    .body(generaleService.getUserInfos(authenticationRequest.getName(), jwt));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred during login.");
        }
    }

    public ResponseEntity<String> resendEmailVerification(String email) {

        Students student = studentsRepository.findByEmail(email).orElse(null);
        if (student != null) {
            if (student.isEnabled())
                return ResponseEntity.badRequest().body("This account has already been verified, please, login.");

            sendEmailVerification(student, null);
            return ResponseEntity.ok("Please, check your email for to complete your registration");
        }

        Instructors instructor = instructorsRepository.findByEmail(email).orElse(null);
        if (instructor != null) {
            if (instructor.isEnabled())
                return ResponseEntity.badRequest().body("This account has already been verified, please, login.");

            sendEmailVerification(null, instructor);
            return ResponseEntity.ok("Please, check your email for to complete your registration");
        }

        return ResponseEntity.status(HttpStatus.SC_GONE).body("user not found");
    }

    private void sendEmailVerification(Students student, Instructors instructor) {

        String generateVerificationToken = UUID.randomUUID().toString();
        if (student != null) {
            student.setVerificationCode(generateVerificationToken);
            student.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));

            studentsRepository.save(student);
            String url = urlOfRequest + generateVerificationToken;

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
