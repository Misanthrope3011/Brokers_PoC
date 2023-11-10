package com.example.kafka_demo.config.configuration.properties;

import com.example.kafka_demo.dto.Credentials;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("common.security")
public record CredentialsConfig(Credentials rabbitmq, Credentials kafka, Credentials pulsar) {


}
