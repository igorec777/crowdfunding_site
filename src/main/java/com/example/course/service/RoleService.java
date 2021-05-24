package com.example.course.service;

import com.example.course.models.Role;
import com.example.course.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RoleService
{
    @Autowired
    private RoleRepository roleRepository;

    public Role createOrFoundRoleByName(String rolename)
    {
        Role role = roleRepository.findByName(rolename);

        if (role == null)
        {
            role = new Role(rolename);
            roleRepository.saveAndFlush(role);

            return role;
        }
        return role;
    }
}
