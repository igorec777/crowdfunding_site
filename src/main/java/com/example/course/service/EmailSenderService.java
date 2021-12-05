package com.example.course.service;

import com.example.course.models.PasswordResetToken;
import com.example.course.models.SecureToken;
import com.example.course.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("javaMailSenderGmail")
    private JavaMailSender gmailSender;

    @Autowired
    @Qualifier("javaMailSenderMailRu")
    private JavaMailSender mailRuSender;

    @Autowired
    @Qualifier("javaMailSenderYandex")
    private JavaMailSender yandexSender;

    public void sendVerificationEmail(User user) {
        SecureToken secureToken = secureTokenService.createSecureToken();

        sendEmail(user.getEmail(), "Verify email",
                String.format("Hello, %s! Go to http://localhost:8081/verify/?token=%s to verify your email",
                user.getUsername(), secureToken.getToken()), gmailSender);

        SecureToken oldToken = secureTokenService.findByUserId(user.getId());
        if (oldToken != null) {
            secureTokenService.deleteById(oldToken.getId());
        }
        userService.save(user);
        secureToken.setUser(user);
        secureTokenService.save(secureToken);
    }

    public void sendResetPasswordEmail(String email) {
        PasswordResetToken passwordResetToken = passwordResetTokenService.create();

        sendEmail(email, "Reset password",
                String.format("Go to http://localhost:8081/changePassword/?token=%s to reset your password",
                        passwordResetToken.getToken()), gmailSender);

        User user = userService.findByEmail(email);
        PasswordResetToken oldToken = passwordResetTokenService.findByUserId(user.getId());

        if (oldToken != null) {
            passwordResetTokenService.deleteByUserId(oldToken.getUser().getId());
        }
        passwordResetToken.setUser(user);
        passwordResetTokenService.save(passwordResetToken);
    }

    private void sendEmail(String toEmail, String subject, String text, JavaMailSender sender) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toEmail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);
        sender.send(simpleMailMessage);
    }
}
