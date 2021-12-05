package com.example.course.service;

import com.example.course.models.PasswordResetToken;
import com.example.course.models.SecureToken;
import com.example.course.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.course.helpers.TokenGeneratorHelper.generateToken;

@Service
public class PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetToken create() {
        String token = generateToken();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        return passwordResetToken;
    }

    public PasswordResetToken save(PasswordResetToken passwordResetToken) {
        return passwordResetTokenRepository.save(passwordResetToken);
    }

    public PasswordResetToken findByUserId(Long id) {
        return passwordResetTokenRepository.findByUserId(id);
    }

    public PasswordResetToken findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Transactional
    public void deleteByUserId(Long id) {
        passwordResetTokenRepository.deleteByUserId(id);
    }
}
