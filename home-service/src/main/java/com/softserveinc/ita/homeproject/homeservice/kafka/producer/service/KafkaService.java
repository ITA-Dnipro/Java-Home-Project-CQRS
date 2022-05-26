package com.softserveinc.ita.homeproject.homeservice.kafka.producer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void produce(String message) {
        System.out.println("Producing the message: " + message);
        kafkaTemplate.send("kafkaTopicMessages", message);
    }

}
