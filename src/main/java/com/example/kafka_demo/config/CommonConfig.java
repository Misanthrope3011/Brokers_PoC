package com.example.kafka_demo.config;

import com.example.kafka_demo.ApplicationConstants;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class CommonConfig {

    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }

}
