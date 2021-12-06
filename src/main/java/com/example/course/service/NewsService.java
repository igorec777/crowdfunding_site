package com.example.course.service;

import com.example.course.models.News;
import com.example.course.models.User;
import com.example.course.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


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

    public List<News> getSortedNews(User user) {
        List<News> newsList = user.getFavoriteCompanies().stream()
                .flatMap(com -> com.getNews().stream())
                .collect(Collectors.toList());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Comparator<News> dateComp = (news2, news1) -> LocalDateTime.parse(news1.getDate(), formatter)
                .isAfter(LocalDateTime.parse(news2.getDate(), formatter)) ? 1 :
                LocalDateTime.parse(news1.getDate(), formatter)
                        .isBefore(LocalDateTime.parse(news2.getDate(), formatter)) ? -1 : 0;
        newsList.sort(dateComp);
        return newsList;
    }
}
