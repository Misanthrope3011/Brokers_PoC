package com.example.kafka_demo.config.configuration.properties;

import com.example.kafka_demo.ApplicationConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("common.topic.config")
public record BrokersConfigProperties(String topicName, int numberOfPartitions, short replicationFactor, String hostName, Long loadSize, String concurrency, boolean truncateOnStartup) {

    public BrokersConfigProperties {
       loadSize = loadSize == null || loadSize <= 0 ? ApplicationConstants.DEFAULT_LOAD_SIZE : loadSize;
    }
}
