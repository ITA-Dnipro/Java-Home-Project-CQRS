package com.softserveinc.ita.homeproject.readerapp.service;

import java.util.List;
import java.util.stream.Collectors;

import com.softserveinc.ita.homeproject.readerapp.controllers.models.ReadNewsReader;
import com.softserveinc.ita.homeproject.readerapp.models.NewsReader;
import com.softserveinc.ita.homeproject.readerapp.repositories.NewReaderRepository;
import com.softserveinc.ita.homeproject.readerapp.service.mapper.ServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsReaderServiceImpl implements NewsReaderService{

    @Autowired
    NewReaderRepository newReaderRepository;

    private final ServiceMapper mapper;

    @Override
    public List<ReadNewsReader> findAll() {
        List<NewsReader> news = newReaderRepository.findAll();
        return news.stream()
                .map(n -> mapper.convert(n, ReadNewsReader.class))
                .collect(Collectors.toList());
    }

    @Override
    public NewsReader createNews(NewsReader newsReader) {
        return newReaderRepository.save(newsReader);
    }


}
