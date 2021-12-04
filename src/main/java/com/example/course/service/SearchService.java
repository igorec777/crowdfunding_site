package com.example.course.service;

import com.example.course.models.Company;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.Query;
import org.hibernate.search.exception.EmptyQueryException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
public class SearchService {
    private final EntityManager entityManager;

    @Autowired
    private CompanyService companyService;

    public List<Company> getCompanyBasedOnWord(String word) {
        FullTextEntityManager fullTextEntityManager =
                Search.getFullTextEntityManager(entityManager);

        QueryBuilder qb = fullTextEntityManager
                .getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Company.class)
                .get();

        try {
            Query query = qb.keyword()
                    .onFields("name", "description", "topic", "tags")
                    .matching(word)
                    .createQuery();
            FullTextQuery fullTextQuery = fullTextEntityManager
                    .createFullTextQuery(query, Company.class);

            return (List<Company>) fullTextQuery.getResultList();
        } catch (EmptyQueryException ex) {
            return companyService.findAll();
        }
    }
}
