package com.example.course.controllers;

import com.example.course.helpers.RegisterHelper;
import com.example.course.models.PasswordResetToken;
import com.example.course.models.SecureToken;
import com.example.course.models.User;
import com.example.course.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import static com.example.course.helpers.RegisterHelper.getCurrentDateTime;
import static com.example.course.helpers.RegisterHelper.passwordEncoder;


@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private SecureTokenService secureTokenService;


    @GetMapping("/login")
    public String loginForm(@ModelAttribute("isPasswordChanged") String isPasswordChanged,
                            Principal principal) {
        if (principal != null) {
            return "companies";
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("duplicateField") String duplicateField,
                               Principal principal) {
        if (principal != null) {
            return "companies";
        }
        return "register";
    }

    @PostMapping("/process_register")
    public String processRegistration(User user, RedirectAttributes rattrs,
                                      Principal principal) {
        if (principal != null) {
            return "companies";
        }
        if (userService.isExistByUsername(user.getUsername())) {
            rattrs.addFlashAttribute("duplicateField", "login");
            return "redirect:/register";
        }
        else if (userService.isExistByEmail(user.getEmail())) {
            rattrs.addFlashAttribute("duplicateField", "email");
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

            emailSenderService.sendVerificationEmail(user);

            return "verify_message";
        }
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam(name = "token", defaultValue = "") String token) {

        SecureToken secureToken = secureTokenService.findByToken(token);
        if (secureToken != null) {
            User user = secureToken.getUser();
            if (user.getRoles().removeIf(r -> r.getName().equals("GUEST"))) {
                user.getRoles().add(roleService.createOrFoundRoleByName("USER"));
                userService.save(user);
                secureTokenService.deleteById(secureToken.getId());
                return "complete_verify";
            }
        }
        return "verify";
    }

    @GetMapping("/resetPassword")
    public String resetPasswordForm(@ModelAttribute("isWrongEmail") String isWrongEmail,
                                    @ModelAttribute("isNotVerified") String isNotVerified,
                                    Principal principal) {
        if (principal != null) {
            return "/companies";
        }
        return "forget_password";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@ModelAttribute("email") String email,
                                RedirectAttributes rattrs) {
        if (!userService.isExistByEmail(email)) {
            rattrs.addFlashAttribute("isWrongEmail", true);
            return "redirect:/resetPassword";
        }
        if (userService.hasRole(userService.findByEmail(email), "GUEST")) {
            rattrs.addFlashAttribute("isNotVerified", true);
            return "redirect:/resetPassword";
        }
        emailSenderService.sendResetPasswordEmail(email);
        return "reset_password_message";
    }

    @GetMapping("/changePassword")
    public String changePasswordForm(@RequestParam(name = "token", defaultValue = "") String token,
                                 Model model) {
        if (token.equals("")) {
            return "redirect:/404";
        }
        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);
        if (passwordResetToken == null) {
            return "redirect:/404";
        }
        model.addAttribute("user", passwordResetToken.getUser());
        return "change_password";
    }

    @PostMapping("/changePassword")
    public String changePassword(@ModelAttribute("email") String email, @ModelAttribute("password") String password,
                                 RedirectAttributes rattrs) {
        User user = userService.findByEmail(email);
        passwordResetTokenService.deleteByUserId(user.getId());
        user.setPassword(password);
        user.setPassword(passwordEncoder(user));
        userService.save(user);
        rattrs.addFlashAttribute("isPasswordChanged", true);
        return "redirect:/login";
    }

    private void createAdmin(User user) {
        user.setPassword(RegisterHelper.passwordEncoder(user));
        user.getRoles().add(roleService.createOrFoundRoleByName("ADMIN"));
        user.setRegisterDate(getCurrentDateTime());
        user.setActive(true);
        userService.save(user);
    }
}
