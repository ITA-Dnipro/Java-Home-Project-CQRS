package com.softserveinc.ita.homeproject.homeservice.kafka;

import java.util.HashMap;
import java.util.Map;

import com.softserve.ita.homeproject.events.NewsAddEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@ConditionalOnProperty(value = "home.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConfig {

    @Bean
    public NewTopic topicHome() {
        return TopicBuilder.name("home")
                .partitions(1)
                .replicas(1)
                .build();
    }

}
