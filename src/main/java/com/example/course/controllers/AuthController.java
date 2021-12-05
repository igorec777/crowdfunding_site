package com.example.course.controllers;

import com.example.course.helpers.RegisterHelper;
import com.example.course.models.SecureToken;
import com.example.course.models.User;
import com.example.course.service.EmailSenderService;
import com.example.course.service.RoleService;
import com.example.course.service.SecureTokenService;
import com.example.course.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.example.course.helpers.RegisterHelper.getCurrentDateTime;


@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private SecureTokenService secureTokenService;


    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/register")
    public String registerForm(@ModelAttribute("duplicateField") String duplicateField) {
        return "register";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/process_register")
    public String processRegistration(User user, RedirectAttributes rattrs) {

        if (userService.isExistByUsername(user.getUsername())) {
            rattrs.addFlashAttribute("duplicateField", "login");
            return "redirect:/register";
        }
//        else if (userService.isExistByEmail(user.getEmail())) {
//            rattrs.addFlashAttribute("duplicateField", "email");
//            return "redirect:/register";
//        }
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
    public String verifyAccount(@RequestParam(name = "token", defaultValue = "") String token, Model model) {
        if (token.equals("")) {
            model.addAttribute("wrongToken", true);
            return "verify";
        }
        else {
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
