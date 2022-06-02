package com.softserveinc.ita.homeproject.homereader.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic topicHome() {
        return TopicBuilder.name("7287f0bv-home")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
