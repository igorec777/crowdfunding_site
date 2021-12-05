package com.example.course.repository;

import com.example.course.models.SecureToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SecureTokenRepository extends JpaRepository<SecureToken, Long> {

    Optional<SecureToken> findByToken(String token);

    @Modifying
    @Query("delete from SecureToken st where st.id = ?1")
    void deleteSecureTokenById(Long id);
}
