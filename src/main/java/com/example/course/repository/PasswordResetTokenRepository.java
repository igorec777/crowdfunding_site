package com.example.course.repository;


import com.example.course.models.PasswordResetToken;
import com.mysql.cj.log.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByUserId(Long id);

    PasswordResetToken findByToken(String token);

    void deleteByUserId(Long id);

}
