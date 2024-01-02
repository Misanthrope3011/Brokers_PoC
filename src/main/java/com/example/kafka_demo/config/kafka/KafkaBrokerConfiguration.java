package com.example.kafka_demo.config.kafka;

import com.example.kafka_demo.ApplicationConstants;
import com.example.kafka_demo.config.configuration.properties.BrokersConfigProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.BytesDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

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
        configProps.put(
                CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,
               "SSL");

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
        props.put("max.poll.records", 10);
        props.put("security.protocol", "SSL");
        if(brokersConfigProperties.isSslEnabled()) {
            props.put("ssl.truststore.location", "/home/user/IdeaProjects/Kafka_Demo/docker/kafka/secure/kafka.truststore.jks");
            props.put("ssl.truststore.password", "password");
            props.put("ssl.keystore.location", "/home/user/IdeaProjects/Kafka_Demo/docker/kafka/secure/kafka.keystore.jks");
            props.put("ssl.keystore.password", "password");
            props.put("ssl.key.password", "password");
        }

        return props;
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
