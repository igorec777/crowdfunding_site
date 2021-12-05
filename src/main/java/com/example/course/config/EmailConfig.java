package com.example.course.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {



    @Bean
    public JavaMailSender javaMailSenderGmail() {
        JavaMailSenderImpl mailSender = initSender();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("igortiger778@gmail.com");
        mailSender.setPassword("davkoihxzmnyyxjw");
        return mailSender;
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.timeout", "4000");
        properties.setProperty("mail.smtp.connectiontimeout", "4000");
        properties.setProperty("mail.smtp.writetimeout", "4000");
        properties.setProperty("mail.debug", "true");

        return properties;
    }

    private JavaMailSenderImpl initSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setProtocol("smtp");
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setJavaMailProperties(getProperties());
        return mailSender;
    }
}
