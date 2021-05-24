package com.example.course.service;

import com.example.course.models.Raiting;
import com.example.course.repository.RaitingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class RaitingService
{
    @Autowired
    private RaitingRepository raitingRepository;

    public void save(Raiting raiting)
    {
        raitingRepository.save(raiting);
    }

    public boolean isExistByUserId(Long id)
    {
        List<Raiting> raitingList = raitingRepository.findAll();

        raitingList.removeIf(raiting -> !raiting.getUser_id().equals(id));

        if (raitingList.size() == 0)
            return false;
        else
            return true;
    }
}
