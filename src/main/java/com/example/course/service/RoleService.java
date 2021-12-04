package com.example.course.service;

import com.example.course.models.Role;
import com.example.course.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role createOrFoundRoleByName(String name) {
        Role role = roleRepository.findByName(name);

        if (role == null) {
            role = new Role(name);
        }
        roleRepository.saveAndFlush(role);
        return role;
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }
}
