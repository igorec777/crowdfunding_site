package com.example.course.service;

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

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject(String.format("Hello %s!", user.getUsername()));
        simpleMailMessage.setText(String.format("Go to http://localhost:8081/verify/?token=%s to verify your account",
                secureToken.getToken()));
        gmailSender.send(simpleMailMessage);

        userService.save(user);
        secureToken.setUser(user);
        secureTokenService.save(secureToken);
    }
}
