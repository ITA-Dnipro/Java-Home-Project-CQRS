package com.softserveinc.ita.homeproject.homeservice.events;

import com.softserve.ita.homeproject.events.NewsAddEvent;
import com.softserve.ita.homeproject.events.NewsDeleteEvent;
import com.softserveinc.ita.homeproject.homedata.general.news.News;
import com.softserveinc.ita.homeproject.homeservice.kafka.KafkaProducer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "home.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class NewsEventProducer {

    private KafkaProducer kafkaProducer;

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public NewsEventProducer(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void sendAddEvent(News news) {
        NewsAddEvent newsAddEvent = modelMapper.map(news, NewsAddEvent.class);
        kafkaProducer.sendEvent(newsAddEvent);
    }

    public void sendDeleteEvent(News news) {
        NewsDeleteEvent newsDeleteEvent = new NewsDeleteEvent(news.getId());
        kafkaProducer.sendEvent(newsDeleteEvent);
    }
}
