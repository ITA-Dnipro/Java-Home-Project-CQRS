package com.softserveinc.ita.homeproject.homeservice.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "home.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class AppEventProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public AppEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void sendTestMessagesOnStartup() {
        for (int i = 1; i <= 20; i++) {
            sendEvent("Test message #" + i);
        }
    }

    public void sendEvent(String message) {
        kafkaTemplate.send("home", message);
    }
}
