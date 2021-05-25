package com.example.course.controllers;

import com.example.course.helpers.RegisterHelper;
import com.example.course.models.Role;
import com.example.course.models.User;
import com.example.course.service.RoleService;
import com.example.course.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;


@Controller
public class AuthController
{
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/login")
    public String loginForm()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
        {
            return "login";
        }
        else
            return "redirect:/";
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("isDuplicate") String isDuplicate)
    {
        if (isDuplicate.equals(""))
            isDuplicate = "0";

        return "register";
    }

    @PostMapping("/process_register")
    public String processRegistration(User user, RedirectAttributes rattrs)
    {
        Set<Role> roles = new HashSet<>();
        Role role;

        if (userService.isExistByUsername(user.getUsername()))
        {
            rattrs.addAttribute("isDuplicate", "1");
            return "redirect:/register";
        }
        else
        {
            user.setPassword(RegisterHelper.passwordEncoder(user));

            role = roleService.createOrFoundRoleByName("USER");

            roles.add(role);

            user.setRoles(roles);

            user.setRegisterDate(RegisterHelper.getCurrentDateTime());
            user.setStatus(1);

            userService.save(user);

            return "process_register";
        }
    }
}
