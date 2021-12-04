package com.example.course.repository;

import com.example.course.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByName(String name);
}
