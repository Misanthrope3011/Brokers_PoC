package com.example.kafka_demo.config;

import com.example.kafka_demo.config.properties.BrokersConfigProperties;
import com.example.kafka_demo.config.properties.CredentialsConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Getter
@Component
public class AppConfigurationProperties {

    private final CredentialsConfig credentialsConfig;
    private final BrokersConfigProperties brokerConsumerConfigs;

}