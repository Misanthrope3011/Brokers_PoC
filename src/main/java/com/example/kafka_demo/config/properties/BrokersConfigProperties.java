package com.example.kafka_demo.config.properties;

import com.example.kafka_demo.ApplicationConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("common.topic.config")
public record BrokersConfigProperties(String topicName, int numberOfPartitions, short replicationFactor, String hostName, Long loadSize) {

    public BrokersConfigProperties {
       loadSize = loadSize == null || loadSize <= 0 ? ApplicationConstants.DEFAULT_LOAD_SIZE : loadSize;
    }
}
