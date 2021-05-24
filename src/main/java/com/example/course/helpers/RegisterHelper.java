package com.example.course.helpers;

import com.example.course.models.Role;
import com.example.course.models.User;
import com.example.course.repository.RoleRepository;
import com.example.course.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

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
