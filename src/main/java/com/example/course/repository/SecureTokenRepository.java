package com.example.course.repository;

import com.example.course.models.SecureToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SecureTokenRepository extends JpaRepository<SecureToken, Long> {

    //Optional<SecureToken> findByTokenAndUserId(String token, Long id);
    Optional<SecureToken> findByToken(String token);
}
