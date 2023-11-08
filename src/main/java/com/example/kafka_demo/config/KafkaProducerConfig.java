package com.example.kafka_demo.config;

import com.example.kafka_demo.ApplicationConstants;
import com.example.kafka_demo.config.properties.BrokersConfigProperties;
import com.example.kafka_demo.dto.DefaultSchema;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.pulsar.client.api.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.pulsar.core.DefaultSchemaResolver;
import org.springframework.pulsar.core.SchemaResolver;


import java.util.HashMap;
import java.util.Map;

import static com.example.kafka_demo.ApplicationConstants.DEFAULT_LOOKUP_BIND;
import static com.example.kafka_demo.ApplicationConstants.SUBSCRIPTION_NAME;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final BrokersConfigProperties brokersProperties;


    @Bean
    public SchemaResolver.SchemaResolverCustomizer<DefaultSchemaResolver> schemaResolverCustomizer() {
        return (schemaResolver) -> {
            schemaResolver.addCustomSchemaMapping(DefaultSchema.class, Schema.JSON(DefaultSchema.class));
        };
    }

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
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public PulsarClient pulsarClient() {
        try {

            return PulsarClient.builder()
                    .dnsLookupBind(DEFAULT_LOOKUP_BIND, 0)
                    .maxConcurrentLookupRequests(5)
                    .serviceUrl(ApplicationConstants.BrokerServicesUrls.PULSAR_ADMIN_URL)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error creating Pulsar client", e);
        }
    }

    @Bean
    public Producer<Object> pulsarProducer() throws PulsarClientException {
        return pulsarClient().newProducer(Schema.JSON(Object.class))
                .topic(brokersProperties.topicName())
                .create();
    }

    @Bean
    public Consumer<byte[]> pulsarConsumer() throws PulsarClientException {
        return pulsarClient().newConsumer(Schema.BYTES)
                .topic(brokersProperties.topicName())
                .subscriptionName(SUBSCRIPTION_NAME)
                .subscriptionType(SubscriptionType.Shared)
                .subscribe();
    }

}
