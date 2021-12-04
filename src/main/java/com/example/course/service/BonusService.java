package com.example.course.service;

import com.example.course.models.Bonus;
import com.example.course.models.User;
import com.example.course.repository.BonusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
public class BonusService {
    @Autowired
    private BonusRepository bonusRepository;

    public void save(Bonus bonus) {
        bonusRepository.saveAndFlush(bonus);
    }

    public List<Bonus> findAll() {
        return bonusRepository.findAll();
    }

    public Bonus findById(Long id) {
        return bonusRepository.findById(id).orElse(null);
    }

    public List<Bonus> findByCompanyId(Long id) {
        List<Bonus> bonuses = findAll();

        bonuses.removeIf(bonus -> !bonus.getCompany().getId().equals(id));

        return bonuses;
    }

    public Set<Bonus> findByUser(User user) {
        return user.getBonuses();
    }

    public boolean isExistByUser(User user, Long id) {
        Set<Bonus> bonuses = user.getBonuses();

        return bonuses.contains(findById(id));
    }
}
