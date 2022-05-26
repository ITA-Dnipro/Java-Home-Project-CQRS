package com.softserveinc.ita.homeproject.homeservice.kafka.producer.controller;

import com.softserveinc.ita.homeproject.homeservice.kafka.producer.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/api/0")
public class KafkaController {

    @Autowired
    private KafkaService kafkaService;

    @GetMapping("/generate")
    public String generate(@RequestParam String message) {
        kafkaService.produce(message);
        return "OK";
    }

}
