package com.example.course.service;

import com.example.course.models.Company;
import com.example.course.models.Raiting;
import com.example.course.repository.RaitingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class RaitingService {
    @Autowired
    private RaitingRepository raitingRepository;

    public void save(Raiting raiting) {
        raitingRepository.save(raiting);
    }

    public boolean isExistByCompanyIdAndUserId(Long companyId, Long userId) {
        return raitingRepository.existsByCompanyIdAndUserId(companyId, userId);
    }

    public Raiting findByCompanyIdAndUserId(Long companyId, Long userId) {
        return raitingRepository.findRaitingByCompanyIdAndUserId(companyId, userId).orElse(null);
    }
}
