package com.example.course.service;

import com.example.course.models.Role;
import com.example.course.models.User;
import com.example.course.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public User findByUsername(String username) {
        return userDetailsService.findByUsername(username);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean isExistByUsername(String username) {
        List<User> users = findAll();

        for (User user : users)
            if (user.getUsername().equals(username))
                return true;
        return false;
    }

    public boolean isHasRole(User user, String role) {
        Set<Role> roles;

        roles = user.getRoles();

        for (Role r : roles) {
            if (r.getName().equals(role)) {
                return true;
            }
        }
        return false;
    }

    public void save(User user) {
        userRepository.saveAndFlush(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
        userRepository.flush();
    }

    public boolean blockById(Long id) {
        User user = findById(id);

        if (user != null && user.getStatus() != 0) {
            user.setStatus(0);
            save(user);
            return true;
        } else
            return false;
    }

    public boolean unblockById(Long id) {
        User user = findById(id);

        if (user != null && user.getStatus() != 1) {
            user.setStatus(1);
            save(user);
            return true;
        } else
            return false;
    }

    public void addRoleById(Long id, Role role) {
        User user = findById(id);

        Set<Role> roles = user.getRoles();

        roles.add(role);

        user.setRoles(roles);

        save(user);
    }

    public void deleteRoleById(Long id, Role role) {
        User user = findById(id);

        Set<Role> roles = user.getRoles();

        roles.remove(role);

        user.setRoles(roles);

        save(user);
    }
}
