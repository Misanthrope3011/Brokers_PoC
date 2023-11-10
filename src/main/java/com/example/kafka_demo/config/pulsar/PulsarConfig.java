package com.example.kafka_demo.config.pulsar;

import com.example.kafka_demo.ApplicationConstants;
import com.example.kafka_demo.config.configuration.properties.BrokersConfigProperties;
import com.example.kafka_demo.dto.DefaultSchema;
import lombok.RequiredArgsConstructor;
import org.apache.pulsar.client.api.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.pulsar.core.DefaultSchemaResolver;
import org.springframework.pulsar.core.SchemaResolver;


import static com.example.kafka_demo.ApplicationConstants.DEFAULT_LOOKUP_BIND;
import static com.example.kafka_demo.ApplicationConstants.SUBSCRIPTION_NAME;

@Configuration
@RequiredArgsConstructor
public class PulsarConfig {

    private final BrokersConfigProperties brokersProperties;


    @Bean
    public SchemaResolver.SchemaResolverCustomizer<DefaultSchemaResolver> schemaResolverCustomizer() {
        return (schemaResolver) -> {
            schemaResolver.addCustomSchemaMapping(DefaultSchema.class, Schema.JSON(DefaultSchema.class));
        };
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
