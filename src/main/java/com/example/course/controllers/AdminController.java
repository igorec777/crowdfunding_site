package com.example.course.controllers;

import com.example.course.models.Role;
import com.example.course.models.User;
import com.example.course.repository.RoleRepository;
import com.example.course.service.RoleService;
import com.example.course.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;


@Controller
public class AdminController
{
    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    RoleRepository roleRepository;

    @GetMapping("/admin/users")
    public String usersList(@ModelAttribute("changesCount") String changesCount, Model model)
    {
        int count;
        List<User> users;

        users = userService.findAll();

        count = users.size();

        model.addAttribute("users", users);
        model.addAttribute("count", count);

        return "users";
    }

    @RequestMapping(value = "/admin/users", method = RequestMethod.POST, params = "delete")
    public String deleteUser(@RequestParam(value = "idChecked", required = false) List<String> checkedUsersId,
                             RedirectAttributes rattrs, Principal principal)
    {
        int changesCount = -1;
        User currUser = userService.findByUsername(principal.getName());

        if (checkedUsersId != null)
        {
            changesCount++;

            for (String id: checkedUsersId)
            {
                if (currUser.getId() != Long.parseLong(id))
                {
                    userService.deleteById(Long.parseLong(id));
                    changesCount++;
                }
            }
        }

        rattrs.addAttribute("changesCount", Long.toString(changesCount));

        return "redirect:/admin/users";
    }

    @RequestMapping(value = "/admin/users", method = RequestMethod.POST, params = "assign")
    public String assignUser(@RequestParam(value = "idChecked", required = false) List<String> checkedUsersId,
                             RedirectAttributes rattrs, Principal principal)
    {
        User currUser = userService.findByUsername(principal.getName());
        User user;
        Role role;
        int changesCount = -1;

        if (checkedUsersId != null)
        {
            changesCount++;

            for (String id: checkedUsersId)
            {
                user = userService.findById(Long.parseLong(id));
                role = roleService.createOrFoundRoleByName("ADMIN");

                if (currUser.getId() != Long.parseLong(id))
                {
                    if (userService.isHasRole(user, "ADMIN"))
                        userService.deleteRoleById(Long.parseLong(id), role);
                    else
                        userService.addRoleById(Long.parseLong(id), role);
                    changesCount++;
                }
            }
        }

        rattrs.addAttribute("changesCount", Long.toString(changesCount));

        return "redirect:/admin/users";
    }

    @RequestMapping(value = "/admin/users", method = RequestMethod.POST, params = "block")
    public String blockUser(@RequestParam(value = "idChecked", required = false) List<String> checkedUsersId,
                            RedirectAttributes rattrs, Principal principal)
    {
        User currUser = userService.findByUsername(principal.getName());
        int changesCount = -1;

        if (checkedUsersId != null)
        {
            changesCount++;

            for (String id: checkedUsersId)
            {
                if (currUser.getId() != Long.parseLong(id))
                {
                    if(userService.blockById(Long.parseLong(id)))
                        changesCount++;
                }
            }
        }

        rattrs.addAttribute("changesCount", Long.toString(changesCount));

        return "redirect:/admin/users";
    }

    @RequestMapping(value = "/admin/users", method = RequestMethod.POST, params = "unblock")
    public String unblockUser(@RequestParam(value = "idChecked", required = false) List<String> checkedUsersId,
                              RedirectAttributes rattrs, Principal principal)
    {
        User currUser = userService.findByUsername(principal.getName());
        int changesCount = -1;

        if (checkedUsersId != null)
        {
            changesCount++;

            for (String id: checkedUsersId)
            {
                if (currUser.getId() != Long.parseLong(id))
                {
                    if(userService.unblockById(Long.parseLong(id)))
                        changesCount++;
                }
            }
        }

        rattrs.addAttribute("changesCount", Long.toString(changesCount));

        return "redirect:/admin/users";
    }
}
