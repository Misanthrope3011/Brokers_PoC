package com.example.kafka_demo.config.kafka;

import com.example.kafka_demo.ApplicationConstants;
import com.example.kafka_demo.config.configuration.properties.BrokersConfigProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.BytesDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaBrokerConfiguration {

    private final BrokersConfigProperties brokersConfigProperties;

    @Bean
    public ProducerFactory<String, byte[]> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                ApplicationConstants.BrokerServicesUrls.KAFKA_HOST);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                ByteArraySerializer.class);
        if(brokersConfigProperties.isSslEnabled()) {
            setCommonSSLProperties(configProps);
        }

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(false);
        return factory;
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("client.id", "kafka_test");
        props.put("group.id", "kafka_test_group");
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", BytesDeserializer.class);
        props.put("auto.offset.reset", "earliest");
        props.put("max.poll.records", 100);
        props.put("buffer.memory", 102400);
        if(brokersConfigProperties.isSslEnabled()) {
            setCommonSSLProperties(props);
        }

        return props;
    }

    private static void setCommonSSLProperties(Map<String, Object> configProps) {
        configProps.put("ssl.truststore.location", "/home/sebastian/IdeaProjects/Kafka_Demo/certs/stores/kafka.truststore.jks");
        configProps.put("ssl.truststore.password", "password");
        configProps.put("ssl.keystore.location", "/home/sebastian/IdeaProjects/Kafka_Demo/certs/stores/kafka.keystore.jks");
        configProps.put("ssl.keystore.password", "password");
        configProps.put("ssl.key.password", "password");
        configProps.put("spring.kafka.ssl.trust-store-type", "PCKS12");
        configProps.put("ssl.endpoint.identification.algorithm", "");
//            props.put("sasl.mechanism", "PLAIN");
//            props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + "username" + "\" password=\"" + "password" + "\";");
        configProps.put("security.protocol", "SSL");
    }

    @Bean
    public KafkaAdmin admin() {
        return new KafkaAdmin(consumerConfigs());
    }

    @Bean
    public DefaultKafkaConsumerFactory<String, byte[]> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
