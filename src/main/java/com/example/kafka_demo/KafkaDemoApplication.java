package com.example.kafka_demo;

import com.example.kafka_demo.config.configuration.properties.BrokersConfigProperties;
import com.example.kafka_demo.config.configuration.properties.CredentialsConfig;
import com.example.kafka_demo.config.configuration.properties.RandomDataProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({BrokersConfigProperties.class, CredentialsConfig.class, RandomDataProperties.class})
public class KafkaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaDemoApplication.class, args);
    }

}
