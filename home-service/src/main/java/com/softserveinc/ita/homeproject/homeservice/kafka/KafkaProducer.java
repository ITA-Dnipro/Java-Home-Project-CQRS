package com.softserveinc.ita.homeproject.homeservice.kafka;

import com.softserve.ita.homeproject.events.AppEvent;
import com.softserve.ita.homeproject.events.NewsAddEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "home.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaProducer {

    private KafkaTemplate<String, AppEvent> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, AppEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(AppEvent event) {
        kafkaTemplate.send("home", event);
    }
}
