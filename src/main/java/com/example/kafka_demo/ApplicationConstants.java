package com.example.kafka_demo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

public class ApplicationConstants {

    public static final int KAFKA_PORT = 9092;
    public static final int RABBITMQ_PORT = 15672;
    public static final int PULSAR_BOOKIE_PORT = 6650;
    public static final int PULSAR_BROKER_PORT = 8080;
    public static final int DEFAULT_LOAD_SIZE = 100;
    public static final String DEFAULT_CLUSTER_NAMESPACE = "public/default";
    public static final String DEFAULT_LOOKUP_BIND = "0.0.0.0";
    public static final String BROKERS_HOST_NAME = "localhost";
    public static final  String SUBSCRIPTION_NAME = "string-topic-subscription";
    public static final  String EXCHANGE = "rabbit-exchange";
    public static final  String DEFAULT_NAMESPACE = "public/default";

    @UtilityClass
    public static final class InvocationPriority {
        public final int HIGHEST = 1;
        public final int MEDIUM = 2;
        public final int LOW = 2;
    }

    public static final String HTTP = "http";
    public static final int THREAD_POOL = 5;


    @Getter
    @AllArgsConstructor
    public enum StringDelimiters {

        COLON(":"),
        DOUBLE_SLASH("//");

        @Override
        public String toString() {
            return value;
        }

        private final String value;
    }


    public static class BrokerServicesUrls {
        public static final String KAFKA_HOST = ApplicationConstants.BROKERS_HOST_NAME + StringDelimiters.COLON + ApplicationConstants.KAFKA_PORT;
        public static final String PULSAR_SERVICE_URL = HTTP + StringDelimiters.COLON.getValue() + StringDelimiters.DOUBLE_SLASH + ApplicationConstants.BROKERS_HOST_NAME + StringDelimiters.COLON.getValue() +  ApplicationConstants.PULSAR_BOOKIE_PORT;
        public static final String PULSAR_ADMIN_URL = HTTP + StringDelimiters.COLON.getValue() + StringDelimiters.DOUBLE_SLASH + ApplicationConstants.BROKERS_HOST_NAME + StringDelimiters.COLON.getValue() +  ApplicationConstants.PULSAR_BROKER_PORT;
    }

}
