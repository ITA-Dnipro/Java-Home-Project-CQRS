package com.softserveinc.ita.homeproject.homereader.service;

import com.softserveinc.ita.homeproject.homereader.model.NewsReader;
import com.softserveinc.ita.homeproject.homereader.model.ReadNews;
import com.softserveinc.ita.homeproject.homereader.repositories.NewsReaderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(SpringExtension.class)
class NewsReaderServiceImplTest {
    @Mock
    private NewsReaderRepository newReaderRepository;

    @InjectMocks
    private NewsReaderServiceImpl newsReaderService;

    private final ModelMapper modelMapper = new ModelMapper();

    @Test
    void findAllNews() {
        NewsReader news = NewsReader.builder()
                .id(1251L)
                .text("text")
                .description("some description")
                .title("title")
                .build();

        NewsReader news2 = NewsReader.builder()
                .id(1251L)
                .text("text")
                .description("some description")
                .title("title")
                .build();

        List<NewsReader> listNews = new ArrayList<>();
        listNews.add(news);
        listNews.add(news2);

        List<ReadNews> expected = listNews.stream()
                .map(n -> modelMapper.map(n, ReadNews.class))
                .collect(Collectors.toList());

        when(newReaderRepository.findAll()).thenReturn(List.of(news, news2));

        List<ReadNews> actual = newsReaderService.findAll();

        assertEquals(expected, actual);
        verify(newReaderRepository, times(1)).findAll();
        verifyNoMoreInteractions(newReaderRepository);
    }

    @Test
    void findNewsById() {
        Long id = 1251L;
        NewsReader news = NewsReader.builder()
                .id(id)
                .text("text")
                .description("some description")
                .title("title")
                .build();

        ReadNews expected = modelMapper.map(news, ReadNews.class);
        when(newReaderRepository.findById(id)).thenReturn(Optional.of(news));

        ReadNews actual = newsReaderService.findById(id);
        assertEquals(expected, actual);
        verify(newReaderRepository, times(1)).findById(id);
        verifyNoMoreInteractions(newReaderRepository);
    }
}
