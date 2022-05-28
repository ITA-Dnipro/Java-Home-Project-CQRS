package com.softserveinc.ita.homeproject.homereader.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AppEventConsumer {

    @KafkaListener(topics = "home")
    public void handleEvent(String message) {
        System.out.println(">>>>>>>>>>>> RECEIVED MESSAGE " + message);
    }
}
