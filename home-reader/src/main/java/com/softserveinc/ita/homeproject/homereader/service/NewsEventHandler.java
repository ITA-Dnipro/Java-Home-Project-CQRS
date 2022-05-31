package com.softserveinc.ita.homeproject.homereader.service;

import com.softserve.ita.homeproject.events.AppEvent;
import com.softserve.ita.homeproject.events.EventType;
import com.softserve.ita.homeproject.events.NewsAddEvent;
import com.softserveinc.ita.homeproject.homereader.model.NewsReader;
import com.softserveinc.ita.homeproject.homereader.repositories.NewsReaderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsEventHandler {

    private final NewsReaderRepository newsReaderRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    public void handleEvent(AppEvent appEvent) {
        NewsReader newsReader = modelMapper.map(appEvent, NewsReader.class);
        if (appEvent.getEventType() == EventType.NEWS_ADD) {
            newsReaderRepository.save(newsReader);
        } else if(appEvent.getEventType() == EventType.NEWS_DELETE){
            newsReaderRepository.deleteById(newsReader.getId());
        }
    }
}
