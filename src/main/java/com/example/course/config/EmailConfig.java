package com.example.course.config;

import org.springframework.beans.factory.annotation.Value;
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

    @Bean
    public JavaMailSender javaMailSenderMailRu() {
        JavaMailSenderImpl mailSender = initSender();
        mailSender.setHost("smtp.mail.ru");
        mailSender.setPort(465);
        mailSender.setUsername("igortiger777@mail.ru");
        mailSender.setPassword("ALeAHh1q5DvX2iihT230");
        return mailSender;
    }

    @Bean
    public JavaMailSender javaMailSenderYandex() {
        JavaMailSenderImpl mailSender = initSender();
        mailSender.setHost("smtp.yandex.ru");
        //mailSender.setPort(587);
        mailSender.setUsername("igortiger778@yandex.ru");
        mailSender.setPassword("wkbzhovyortudssy");
        return mailSender;
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.timeout", "4000");
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
