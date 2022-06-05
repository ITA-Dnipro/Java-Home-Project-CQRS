package com.softserveinc.ita.homeproject.homereader.kafka;

import com.softserve.ita.homeproject.events.AppEvent;
import com.softserveinc.ita.homeproject.homereader.service.NewsEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final NewsEventHandler newsEventHandler;

    @KafkaListener(topics = "${home.kafka.topic}")
    public void handleEvent(AppEvent appEvent) {
        newsEventHandler.handleEvent(appEvent);
    }
}
