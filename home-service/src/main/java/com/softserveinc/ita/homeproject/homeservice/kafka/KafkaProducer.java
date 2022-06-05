package com.softserveinc.ita.homeproject.homeservice.kafka;

import com.softserve.ita.homeproject.events.AppEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "home.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaProducer {

    private final KafkaTemplate<String, AppEvent> kafkaTemplate;

    private final String topicName;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, AppEvent> kafkaTemplate,
                         @Value("${home.kafka.topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendEvent(AppEvent event) {
        kafkaTemplate.send(topicName, event);
    }
}
