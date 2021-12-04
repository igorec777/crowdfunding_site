package com.example.course.repository;

import com.example.course.models.Bonus;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BonusRepository extends JpaRepository<Bonus, Long> {
}
