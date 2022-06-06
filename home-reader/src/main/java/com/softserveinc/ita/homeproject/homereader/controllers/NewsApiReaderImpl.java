package com.softserveinc.ita.homeproject.homereader.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.mongodb.morphia.MorphiaQuery;
import com.softserveinc.ita.homeproject.homereader.api.NewsApi;
import com.softserveinc.ita.homeproject.homereader.model.NewsReader;
import com.softserveinc.ita.homeproject.homereader.model.QNewsReader;
import com.softserveinc.ita.homeproject.homereader.model.ReadNews;
import com.softserveinc.ita.homeproject.homereader.repositories.NewsReaderRepository;
import com.softserveinc.ita.homeproject.homereader.service.NewsReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class NewsApiReaderImpl implements NewsApi {

    @Autowired
    NewsReaderService newsReaderService;

    @Override
    public ResponseEntity<List<ReadNews>> getAllNews(Integer pageNumber,
                                                     Integer pageSize,
                                                     String sort,
                                                     String filter,
                                                     Long id,
                                                     String title,
                                                     String text,
                                                     String source) {

        BooleanExpression predicate = getPredicate(id, title, text, source);
        PageRequest pageable = getPageable(pageNumber, pageSize, sort);
        return ResponseEntity.status(HttpStatus.OK).body(newsReaderService.findAll(predicate, pageable));
    }

    @Override
    public ResponseEntity<ReadNews> getNews(Long id) {
        ReadNews news = newsReaderService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(news);
    }

    private BooleanExpression getPredicate(Long id,
                                           String title,
                                           String text,
                                           String source) {

        QNewsReader qNewsReader = QNewsReader.newsReader;
        BooleanExpression predicate = null;
        if (Optional.ofNullable(id).isPresent()) {
            predicate = qNewsReader.id.eq(id);
        }
        if (Optional.ofNullable(title).isPresent()) {
            if (Optional.ofNullable(predicate).isPresent()) {
                predicate.and(qNewsReader.title.containsIgnoreCase(title));
            }
            predicate = qNewsReader.title.containsIgnoreCase(title);
        }
        if (Optional.ofNullable(text).isPresent()) {
            if (Optional.ofNullable(predicate).isPresent()) {
                predicate.and(qNewsReader.text.containsIgnoreCase(text));
            }
            predicate = qNewsReader.text.containsIgnoreCase(text);
        }
        if (Optional.ofNullable(source).isPresent()) {
            if (Optional.ofNullable(predicate).isPresent()) {
                predicate.and(qNewsReader.source.containsIgnoreCase(source));
            }
            predicate = qNewsReader.source.containsIgnoreCase(source);
        }

        return predicate;
    }

    private PageRequest getPageable(Integer pageNumber,
                                    Integer pageSize,
                                    String sort) {

        List<String> parsing = List.of(sort.split(","));
        Sort sorting = Sort.by(Sort.Direction.fromString(parsing.get(1)), parsing.get(0));

        return PageRequest.of(pageNumber-1, pageSize, sorting);
    }
}
