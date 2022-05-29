package com.softserveinc.ita.homeproject.homereader.service;

import com.softserve.ita.homeproject.events.AppEvent;
import com.softserve.ita.homeproject.events.EventType;
import com.softserve.ita.homeproject.events.NewsAddEvent;
import com.softserveinc.ita.homeproject.homereader.model.NewsReader;
import com.softserveinc.ita.homeproject.homereader.repositories.NewsReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsEventHandler {

    private final NewsReaderRepository newsReaderRepository;

    public void handleEvent(AppEvent appEvent) {
        if (appEvent.getEventType() == EventType.NEWS_ADD) {
            NewsAddEvent newsAddEvent = (NewsAddEvent) appEvent;
            NewsReader newsReader = new NewsReader();
            newsReader.setId(newsAddEvent.getId());
            newsReader.setTitle(newsAddEvent.getTitle());
            newsReader.setText(newsAddEvent.getText());
            newsReader.setDescription(newsAddEvent.getDescription());
            newsReader.setEnabled(newsAddEvent.getEnabled());
            newsReaderRepository.save(newsReader);
        }
    }

}
