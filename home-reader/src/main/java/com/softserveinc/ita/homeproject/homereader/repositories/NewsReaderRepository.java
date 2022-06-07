package com.softserveinc.ita.homeproject.homereader.repositories;

import com.softserveinc.ita.homeproject.homereader.model.NewsReader;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface NewsReaderRepository extends MongoRepository<NewsReader, Long>,
        QuerydslPredicateExecutor<NewsReader> {

    @DeleteQuery
    void deleteById(Long id);
}
