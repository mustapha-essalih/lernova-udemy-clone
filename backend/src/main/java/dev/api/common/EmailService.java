package dev.api.common;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import dev.api.user.model.Instructors;
import dev.api.user.model.Student;
import dev.api.user.repository.InstructorsRepository;
import dev.api.user.repository.StudentsRepository;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private JavaMailSender mailSender;
    private StudentsRepository studentsRepository;
    private InstructorsRepository instructorsRepository;

    public EmailService(JavaMailSender mailSender, StudentsRepository studentsRepository,
            InstructorsRepository instructorsRepository) {
        this.mailSender = mailSender;
        this.studentsRepository = studentsRepository;
        this.instructorsRepository = instructorsRepository;
    }

    @Value("${spring.mail.username}")
    private String from;

    @Async
    public void sendEmailVerification(String username, String email, String url) {
        String subject = "Email Verification";
        String senderName = "User Registration Portal Service";
        String mailContent = "<p> Hi, " + username + ", </p>" +
                "<p>Thank you for registering with us," + "" +
                "Please, follow the link below to complete your registration.</p>" +
                "<a href=\"" + url + "\">Verify your email to activate your account</a>" +
                "<p> Thank you <br> Users Registration Portal Service";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        try {
            messageHelper.setFrom(from, senderName);
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(mailContent, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("error in sending email");
        }
    }

    // public void sendPasswordResetVerificationEmail(User user, String url) {
    // String subject = "Password Reset Request Verification";
    // String senderName = "User Registration Portal Service";
    // String mailContent = "<p> Hi, " + user.getUsername()+ ", </p>"+
    // "<p><b>You recently requested to reset your password,</b>"+"" +
    // "Please, follow the link below to complete the action.</p>"+
    // "<a href=\"" + url+ "\">Reset password</a>"+
    // "<p> Users Registration Portal Service";
    // MimeMessage message = mailSender.createMimeMessage();
    // try {
    // var messageHelper = new MimeMessageHelper(message);
    // messageHelper.setFrom(from, senderName);
    // messageHelper.setTo(user.getEmail());
    // messageHelper.setSubject(subject);
    // messageHelper.setText(mailContent, true);
    // mailSender.send(message);

    // } catch (Exception e) {
    // throw new RuntimeException("error in sending email");
    // }
    // }

    public ResponseEntity<ApiResponse<String>> emailVerification(String token) {

        Student student = studentsRepository.findByVerificationCode(token).orElse(null);
        if (student != null) {
            if (student.isEnabled())
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "This account has already been verified, please, login.", null));

            LocalDateTime expiresAt = student.getVerificationCodeExpiresAt();

            if (expiresAt.isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Token expired", null));
            }

            student.setEnabled(true);
            studentsRepository.save(student);
            return ResponseEntity.ok(new ApiResponse<>(true, "Email verified successfully. Now you can login to your account", null));
        }

        Instructors instructor = instructorsRepository.findByVerificationCode(token).orElse(null);

        if (instructor != null) {

            if (instructor.isEnabled())
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "This account has already been verified, please, login.", null));

            LocalDateTime expiresAt = instructor.getVerificationCodeExpiresAt();

            if (expiresAt.isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Token expired", null));
            }

            instructor.setEnabled(true);
            instructorsRepository.save(instructor);
            return ResponseEntity.ok(new ApiResponse<>(true, "Email verified successfully. Now you can login to your account", null));
        }

        return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Invalid token", null));

    }

}
