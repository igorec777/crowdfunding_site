package com.example.course.service;

import com.example.course.models.News;
import com.example.course.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NewsService {
    @Autowired
    private NewsRepository newsRepository;

    public void save(News news) {
        newsRepository.saveAndFlush(news);
    }

    public List<News> findAll() {
        return newsRepository.findAll();
    }

    public List<News> findByCompanyId(Long id) {
        List<News> news = findAll();

        news.removeIf(n -> !n.getCompany().getId().equals(id));

        return news;
    }
}
