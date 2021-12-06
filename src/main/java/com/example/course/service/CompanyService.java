package com.example.course.service;

import com.example.course.models.Company;
import com.example.course.models.Raiting;
import com.example.course.models.User;
import com.example.course.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.example.course.helpers.DonateHelper.round;


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

    public boolean isExistByCompanyName(String name) {
        return companyRepository.existsByName(name);
    }

    public List<Company> findByTopic(String topic) {
        List<Company> companies = findAll();

        companies.removeIf(com -> !com.getTopic().equals(topic));

        return companies;
    }

    public float getAverageRatingByCompany(Company company) {
        Set<Raiting> raitings = company.getRaitings();
        int total = 0;
        if (raitings.isEmpty()) {
            return total;
        }
        for (Raiting raiting : raitings) {
            total += raiting.getValue();
        }
        return round((float) total / raitings.size(), 2);
    }

    public void calculateAverageForCompanies(List<Company> companies) {
        companies.forEach(com -> com.setAverageRate(getAverageRatingByCompany(com)));
    }

    public Company findById(Long id) {
        return companyRepository.findById(id).orElse(null);
    }

    public void save(Company company) {
        companyRepository.saveAndFlush(company);
    }

    public void deleteById(Long id) {
        if (companyRepository.findById(id).isPresent()) {
            Company company = companyRepository.findById(id).get();
            company.getBackers().forEach(user -> user.getBackedCompanies().removeIf(com -> com.getId().equals(id)));
            company.getLikedUsers().forEach(user -> user.getFavoriteCompanies().removeIf(com -> com.getId().equals(id)));
            companyRepository.save(company);
            companyRepository.delete(company);
        }
    }
}
