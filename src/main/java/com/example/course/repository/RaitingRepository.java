package com.example.course.repository;

import com.example.course.models.Raiting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RaitingRepository extends JpaRepository<Raiting, Long> {
    boolean existsByCompanyIdAndUserId(Long companyId, Long userId);

    Optional<Raiting> findRaitingByCompanyIdAndUserId(Long companyId, Long userId);
}
