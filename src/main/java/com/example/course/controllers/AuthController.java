package com.example.course.controllers;

import com.example.course.helpers.RegisterHelper;
import com.example.course.models.Role;
import com.example.course.models.SecureToken;
import com.example.course.models.User;
import com.example.course.service.RoleService;
import com.example.course.service.SecureTokenService;
import com.example.course.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;

import static com.example.course.helpers.RegisterHelper.getCurrentDateTime;

@PreAuthorize("isAnonymous()")
@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SecureTokenService secureTokenService;


    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("isDuplicate") String isDuplicate) {
        if (isDuplicate.equals(""))
            isDuplicate = "0";
        return "register";
    }

    @PostMapping("/process_register")
    public String processRegistration(User user, RedirectAttributes rattrs) {
        Set<Role> roles = new HashSet<>();
        Role role;

        if (userService.isExistByUsername(user.getUsername())) {
            rattrs.addAttribute("isDuplicate", "1");
            return "redirect:/register";
        }
        else if (userService.findAll().isEmpty()) {
            createAdmin(user);
            return "redirect:/";
        }
        else {
            user.setPassword(RegisterHelper.passwordEncoder(user));
            user.getRoles().add(roleService.createOrFoundRoleByName("GUEST"));
            user.setRegisterDate(getCurrentDateTime());
            user.setActive(true);
            userService.save(user);

            //todo sending email
            userService.sendVerificationEmail(user);

            return "verify";
        }
    }

    @PreAuthorize("isAuthenticated() and hasAuthority('GUEST')")
    @GetMapping("/verify")
    public String verifyAccount(@RequestParam(name = "token") String token) {
        SecureToken secureToken = secureTokenService.findByToken(token);
        if (secureToken != null) {
            User user = secureToken.getUser();
            user.getRoles().removeIf(r -> r.getName().equals("GUEST"));
            user.getRoles().add(roleService.createOrFoundRoleByName("USER"));
            userService.save(user);
            secureTokenService.deleteById(secureToken.getId());
            return "complete_verify";
        }
        else {
            return "redirect:/404";
        }
    }

    private void createAdmin(User user) {
        user.setPassword(RegisterHelper.passwordEncoder(user));
        user.getRoles().add(roleService.createOrFoundRoleByName("ADMIN"));
        user.setRegisterDate(getCurrentDateTime());
        user.setActive(true);
        userService.save(user);
    }
}
