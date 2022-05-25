package com.softserveinc.ita.homeproject.homeservice.kafka.consumer;

import com.softserveinc.ita.homeproject.homeservice.kafka.model.Model;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    @KafkaListener(topics = "kafkaTopicMessages", groupId = "message_group_id")
    public void consume(String message){
        System.out.println("Consuming the message: "+ message);
    }

    @KafkaListener(topics = "kafkaTopicObjectMessages", groupId = "message_group_id")
    public void consumeObject(Model model){
        System.out.println("Consuming the messageObject: "+ model);
    }
}
