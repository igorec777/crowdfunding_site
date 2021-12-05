package com.example.course.service;

import com.example.course.models.Bonus;
import com.example.course.models.Company;
import com.example.course.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CompanyService {
    @Autowired
    CompanyRepository companyRepository;

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public List<Company> findByUserId(Long id) {
        List<Company> companies = findAll();

        companies.removeIf(com -> !com.getUser().getId().equals(id));
        return companies;
    }

    public int getBackersCount(Company company) {
        int backers = 0;
        for (Bonus bonus : company.getBonuses()) {
            backers += bonus.getUsers().size();
        }
        return backers;
    }

    public boolean isExistByCompanyName(String name) {
        return companyRepository.existsByName(name);
    }

    public List<Company> findByTopic(String topic) {
        List<Company> companies = findAll();

        companies.removeIf(com -> !com.getTopic().equals(topic));

        return companies;
    }

    public Company findById(Long id) {
        return companyRepository.findById(id).orElse(null);
    }

    public void save(Company company) {
        companyRepository.saveAndFlush(company);
    }

    public void deleteById(Long id) {
        companyRepository.deleteById(id);
    }
}
