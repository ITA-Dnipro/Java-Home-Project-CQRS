package com.softserveinc.ita.homeproject.homeservice.kafka.producer.service;

import com.softserveinc.ita.homeproject.homeservice.kafka.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, Model> kafkaObjectTemplate;

    public void produce(String message) {
        System.out.println("Producing the message: " + message);
        kafkaTemplate.send("kafkaTopicMessages", message);
    }

    public void produceObject(Model model) {
        System.out.println("Producing the Objectmessage: " + model);
        kafkaObjectTemplate.send("kafkaTopicObjectMessages", model);
    }
}
