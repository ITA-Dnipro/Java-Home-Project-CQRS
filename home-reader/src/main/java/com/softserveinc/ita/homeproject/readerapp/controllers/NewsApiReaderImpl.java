package com.softserveinc.ita.homeproject.readerapp.controllers;

import java.util.List;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.softserveinc.ita.homeproject.readerapp.controllers.models.ReadNewsReader;
import com.softserveinc.ita.homeproject.readerapp.models.NewsReader;
import com.softserveinc.ita.homeproject.readerapp.service.NewsReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewsApiReaderImpl implements NewsApiReader{

    @Autowired
    NewsReaderService newsReaderService;

    @Override
    @GetMapping("/news")
    public Response getAllNews() {
        List<ReadNewsReader> readNews = newsReaderService.findAll();
        return Response.status(Response.Status.OK)
                .entity(readNews)
                .build();
    }

    @Override
    public Response getNews(Long id) {
        return null;
    }

    @Override
    @PostMapping("/news")
    public Response createNews(@RequestBody NewsReader createNews) {
        return Response.status(Response.Status.CREATED)
                .entity(newsReaderService.createNews(createNews))
                .build();
    }


}
