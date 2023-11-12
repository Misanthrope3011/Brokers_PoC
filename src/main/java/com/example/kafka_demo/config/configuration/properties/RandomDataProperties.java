package com.example.kafka_demo.config.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("common.data.utils")
public record RandomDataProperties(int imageSizeBytes, int nameSizeBytes, int descSizeBytes) {


}
