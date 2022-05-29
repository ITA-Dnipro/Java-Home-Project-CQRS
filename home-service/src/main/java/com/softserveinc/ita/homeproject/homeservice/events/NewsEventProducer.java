package com.softserveinc.ita.homeproject.homeservice.events;

import com.softserve.ita.homeproject.events.NewsAddEvent;
import com.softserveinc.ita.homeproject.homedata.general.news.News;
import com.softserveinc.ita.homeproject.homeservice.kafka.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "home.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class NewsEventProducer {

    private KafkaProducer kafkaProducer;

    @Autowired
    public NewsEventProducer(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void sendAddEvent(News news) {
        NewsAddEvent newsAddEvent = new NewsAddEvent();
        newsAddEvent.setId(news.getId());
        newsAddEvent.setTitle(news.getTitle());
        newsAddEvent.setText(news.getText());
        newsAddEvent.setDescription(news.getDescription());
        newsAddEvent.setEnabled(news.getEnabled());
        kafkaProducer.sendEvent(newsAddEvent);
    }
}
