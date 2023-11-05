package com.example.kafka_demo.config;

import com.example.kafka_demo.data.MainEntity;
import com.example.kafka_demo.dto.User;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.shade.com.google.common.primitives.Bytes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.pulsar.core.DefaultSchemaResolver;
import org.springframework.pulsar.core.SchemaResolver;

import java.util.HashMap;
import java.util.Map;

import static com.example.kafka_demo.service.PulsarConsumerService.STRING_TOPIC;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public SchemaResolver.SchemaResolverCustomizer<DefaultSchemaResolver> schemaResolverCustomizer() {
        return (schemaResolver) -> {
            schemaResolver.addCustomSchemaMapping(User.class, Schema.JSON(User.class));
        };
    }

    @Bean
    public ProducerFactory<String, byte[]> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092");
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                ByteArraySerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public PulsarClient pulsarClient() {
        try {
            String serviceUrl = "pulsar://localhost:6650";
            return PulsarClient.builder()
                    .dnsLookupBind("0.0.0.0", 0)
                    .serviceUrl(serviceUrl)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error creating Pulsar client", e);
        }
    }

    @Bean
    public Producer<Object> pulsarProducer() throws PulsarClientException {
        return pulsarClient().newProducer(Schema.JSON(Object.class))
                .topic(STRING_TOPIC)
                .create();
    }

//    @Bean
//    public Consumer<byte[]> pulsarConsumer() throws PulsarClientException {
//        return pulsarClient().newConsumer(Schema.BYTES)
//                .topic(STRING_TOPIC)
//                .subscriptionName("string-topic-subscription")
//                .subscribe();
//    }

}
