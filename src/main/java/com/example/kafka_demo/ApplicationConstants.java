package com.example.kafka_demo;

import com.example.kafka_demo.data.ThroughputData;
import lombok.AllArgsConstructor;

public class ApplicationConstants {

    public static final int KAFKA_PORT = 9092;
    public static final int RABBITMQ_PORT = 15672;
    public static final int PULSAR_BOOKIE_PORT = 6650;
    public static final String DEFAULT_LOOKUP_BIND = "0.0.0.0";
    public static final String BROKERS_HOST_NAME = "localhost";

    @AllArgsConstructor
    public enum StringDelimiters {
        COLON(":"),
        DOUBLE_SLASH("//");

        private final String value;
    }

    public static class BrokerServicesUrls {
        public static final String KAFKA_HOST = ApplicationConstants.BROKERS_HOST_NAME + StringDelimiters.COLON + ApplicationConstants.KAFKA_PORT;
        public static final String PULSAR_HOST = ThroughputData.BrokerDomain.PULSAR.name() + StringDelimiters.COLON + StringDelimiters.DOUBLE_SLASH + ApplicationConstants.BROKERS_HOST_NAME + ApplicationConstants.PULSAR_BOOKIE_PORT;

    }

}
