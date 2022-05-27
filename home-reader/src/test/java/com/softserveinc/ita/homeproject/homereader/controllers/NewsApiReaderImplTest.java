package com.softserveinc.ita.homeproject.homereader.controllers;

import com.softserveinc.ita.homeproject.homereader.model.ReadNews;
import com.softserveinc.ita.homeproject.homereader.service.NewsReaderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
class NewsApiReaderImplTest {

    private MockMvc mockMvc;

    @InjectMocks
    NewsApiReaderImpl controller;

    @Mock
    NewsReaderServiceImpl service;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void getAllNewsTest() throws Exception {
        ReadNews news = new ReadNews();
        news.setId(1235L);
        news.setTitle("Title");
        news.setDescription("Description");

        when(service.findAll()).thenReturn(List.of(news));

        mockMvc.perform(get("http://localhost:8099/news")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void getNewsById() throws Exception {
        Long id = 1251L;
        ReadNews news = new ReadNews();
        news.setId(id);
        news.setTitle("Title");
        news.setDescription("Description");

        when(service.findById(id)).thenReturn(news);

        mockMvc.perform(get("http://localhost:8099/news/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).findById(id);
        verifyNoMoreInteractions(service);
    }

}
