package com.example.course.service;

import com.example.course.models.SecureToken;
import com.example.course.repository.SecureTokenRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

@Service
public class SecureTokenService {

    @Autowired
    private SecureTokenRepository secureTokenRepository;

    public static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(64);

    public SecureToken createSecureToken() {
        String token = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()));
        SecureToken secureToken = new SecureToken();
        secureToken.setToken(token);
        this.save(secureToken);
        return secureToken;
    }

    public void save(SecureToken secureToken) {
        secureTokenRepository.save(secureToken);
    }

    public SecureToken findByToken(String token) {
        return secureTokenRepository.findByToken(token).orElse(null);
    }

    public void deleteById(Long id) {
        secureTokenRepository.deleteById(id);
    }

//    public SecureToken findByTokenAndUserId(String token, Long id) {
//        return secureTokenRepository.findByTokenAndUserId(token, id).orElse(null);
//    }
}
