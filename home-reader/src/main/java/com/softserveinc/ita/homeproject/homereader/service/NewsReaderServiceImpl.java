package com.softserveinc.ita.homeproject.homereader.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.softserveinc.ita.homeproject.homereader.model.NewsReader;
import com.softserveinc.ita.homeproject.homereader.model.ReadNews;
import com.softserveinc.ita.homeproject.homereader.repositories.NewsReaderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsReaderServiceImpl implements NewsReaderService {

    @Autowired
    NewsReaderRepository newReaderRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<ReadNews> findAll() {
        List<NewsReader> news = newReaderRepository.findAll();
        return news.stream()
                .map(n -> modelMapper.map(n, ReadNews.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadNews> findAll(BooleanExpression predicate,
                                  PageRequest pageable) {

        if (Optional.ofNullable(predicate).isPresent()) {
            return newReaderRepository.findAll(predicate, pageable)
                    .map(n -> modelMapper.map(n, ReadNews.class))
                    .stream().collect(Collectors.toList());
        } else {
            return newReaderRepository.findAll(pageable)
                    .map(n -> modelMapper.map(n, ReadNews.class))
                    .stream().collect(Collectors.toList());
        }
    }

    @Override
    public ReadNews findById(Long id) {
        Optional<NewsReader> readNews = newReaderRepository.findById(id);
        return readNews.map(newsReader -> modelMapper.map(newsReader, ReadNews.class)).orElse(null);
    }
}
