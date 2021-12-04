package com.example.course.controllers;

import com.example.course.models.Role;
import com.example.course.models.User;
import com.example.course.service.RoleService;
import com.example.course.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Controller
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    @Autowired
    UserService userService;
    //@Autowired
    //AuthenticationFacade authentication;
    @Autowired
    RoleService roleService;

    @GetMapping("/admin/users")
    public String usersList(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    @PostMapping(value = "/admin/users", params = "delete")
    public String deleteUser(@RequestParam(value = "idChecked", required = false) List<String> checkedUsersId,
                             Principal principal) {

        User currUser = userService.findByUsername(principal.getName());
        if (checkedUsersId != null) {
            for (String id : checkedUsersId) {
                if (currUser.getId() != Long.parseLong(id)) {
                    userService.deleteById(Long.parseLong(id));
                }
            }
        }
        return "redirect:/admin/users";
    }

    @PostMapping(value = "/admin/users", params = "assign")
    public String assignUser(@RequestParam(value = "idChecked", required = false) List<String> checkedUsersId,
                             Principal principal) {

        User currUser = userService.findByUsername(principal.getName());
        Role role;

        if (checkedUsersId != null) {
            for (String id : checkedUsersId) {
                User user = userService.findById(Long.parseLong(id));
                role = roleService.createOrFoundRoleByName("ADMIN");

                if (currUser.getId() != Long.parseLong(id)) {
                    if (userService.isHasRole(user, "ADMIN"))
                        userService.deleteRoleById(Long.parseLong(id), role);
                    else
                        userService.addRoleById(Long.parseLong(id), role);
                }
            }
        }
        return "redirect:/admin/users";
    }

    @PostMapping(value = "/admin/users", params = "block")
    public String blockUser(@RequestParam(value = "idChecked", required = false) List<String> checkedUsersId,
                            Principal principal) {

        User currUser = userService.findByUsername(principal.getName());
        if (checkedUsersId != null) {
            for (String id : checkedUsersId) {
                if (currUser.getId() != Long.parseLong(id)) {
                    userService.blockById(Long.parseLong(id));
                }
            }
        }
        return "redirect:/admin/users";
    }

    @PostMapping(value = "/admin/users", params = "unblock")
    public String unblockUser(@RequestParam(value = "idChecked", required = false) List<String> checkedUsersId,
                              Principal principal) {

        User currUser = userService.findByUsername(principal.getName());
        if (checkedUsersId != null) {
            for (String id : checkedUsersId) {
                if (currUser.getId() != Long.parseLong(id)) {
                    userService.unblockById(Long.parseLong(id));
                }
            }
        }
        return "redirect:/admin/users";
    }
}
