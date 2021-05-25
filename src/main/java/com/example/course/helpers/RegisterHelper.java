package com.example.course.helpers;

import com.example.course.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class RegisterHelper
{
    public static String getCurrentDateTime()
    {
        DateTimeFormatter dtf;
        String registerDateTime = "";

        dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd");
        registerDateTime += dtf.format(LocalDate.now());

        dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        registerDateTime += " " + dtf.format(LocalTime.now());

        return registerDateTime;
    }

    public static String passwordEncoder(User user)
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return encoder.encode(user.getPassword());
    }
}
