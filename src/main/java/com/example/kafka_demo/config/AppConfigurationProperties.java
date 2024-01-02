package com.example.kafka_demo.config;

import com.example.kafka_demo.config.configuration.properties.BrokersConfigProperties;
import com.example.kafka_demo.config.configuration.properties.CredentialsConfig;
import com.example.kafka_demo.config.configuration.properties.RandomDataProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Getter
@Component
public class AppConfigurationProperties {

    private final CredentialsConfig credentialsConfig;
    private final BrokersConfigProperties brokerConsumerConfigs;
    private final RandomDataProperties randomDataProperties;

}
