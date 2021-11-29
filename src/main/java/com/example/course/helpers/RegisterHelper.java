package com.example.course.helpers;

import com.example.course.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.*;
import java.time.format.DateTimeFormatter;


public class RegisterHelper {
    public static String getCurrentDateTime() {
        DateTimeFormatter dtf;
        String registerDateTime = "";

        dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        registerDateTime += dtf.format(LocalDateTime.now().atZone(ZoneId.of("UTC+3")));

        dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        registerDateTime += " " + dtf.format(LocalDateTime.now().atZone(ZoneId.of("UTC+3")));

        return registerDateTime;
    }

    public static String passwordEncoder(User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return encoder.encode(user.getPassword());
    }
}
