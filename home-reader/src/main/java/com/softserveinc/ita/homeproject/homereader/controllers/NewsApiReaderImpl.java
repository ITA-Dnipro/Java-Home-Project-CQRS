package com.softserveinc.ita.homeproject.homereader.controllers;

import java.util.List;

import com.softserveinc.ita.homeproject.homereader.api.NewsApi;
import com.softserveinc.ita.homeproject.homereader.model.ReadNews;
import com.softserveinc.ita.homeproject.homereader.service.NewsReaderService;
import org.springframework.beans.factory.annotation.Autowired;
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
        return ResponseEntity.status(HttpStatus.OK).body(newsReaderService.findAll());
    }

    @Override
    public ResponseEntity<ReadNews> getNews(Long id) {
        ReadNews news = newsReaderService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(news);
    }
}
