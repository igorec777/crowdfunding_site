package com.example.course.service;

import com.example.course.models.User;
import com.example.course.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        return userDetails.build();

    }
}
