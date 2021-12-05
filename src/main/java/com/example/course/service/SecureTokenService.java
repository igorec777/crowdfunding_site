package com.example.course.service;

import com.example.course.models.SecureToken;
import com.example.course.repository.SecureTokenRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.course.helpers.TokenGeneratorHelper.generateToken;

@Service
public class SecureTokenService {

    @Autowired
    private SecureTokenRepository secureTokenRepository;


    public SecureToken createSecureToken() {
        String token = generateToken();
        SecureToken secureToken = new SecureToken();
        secureToken.setToken(token);
        return secureToken;
    }

    public void save(SecureToken secureToken) {
        secureTokenRepository.save(secureToken);
    }

    public SecureToken findByToken(String token) {
        return secureTokenRepository.findByToken(token).orElse(null);
    }

    public SecureToken findByUserId(Long id) {
        return secureTokenRepository.findByUserId(id);
    }

    @Transactional
    public void deleteById(Long id) {
        secureTokenRepository.deleteSecureTokenById(id);
        secureTokenRepository.flush();
    }
}
