package com.example.kafka_demo.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
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
