package com.example.kafka_demo.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("common.topic.config")
public record BrokersConfig(String topicName, int numberOfPartitions, short replicationFactor, String hostName) {

}
