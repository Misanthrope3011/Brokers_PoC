package com.example.kafka_demo.config.pulsar;

import com.example.kafka_demo.ApplicationConstants;
import com.example.kafka_demo.ApplicationException;
import com.example.kafka_demo.config.configuration.properties.BrokersConfigProperties;
import com.example.kafka_demo.dto.DefaultSchema;
import lombok.RequiredArgsConstructor;
import org.apache.pulsar.client.api.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.pulsar.core.DefaultSchemaResolver;
import org.springframework.pulsar.core.SchemaResolver;

import static com.example.kafka_demo.ApplicationConstants.DEFAULT_LOOKUP_BIND;
import static com.example.kafka_demo.ApplicationConstants.SUBSCRIPTION_NAME;


@Configuration
@RequiredArgsConstructor
public class PulsarConfig {

    private final BrokersConfigProperties brokersProperties;
    private static final String DLQ_PREFIX = "dlq.";


    @Bean
    public SchemaResolver.SchemaResolverCustomizer<DefaultSchemaResolver> schemaResolverCustomizer() {
        return (schemaResolver) -> {
            schemaResolver.addCustomSchemaMapping(DefaultSchema.class, Schema.JSON(DefaultSchema.class));
        };
    }

    @Bean
    public PulsarClient pulsarClient() throws PulsarClientException {
            ClientBuilder client =  PulsarClient.builder()
                    .dnsLookupBind(DEFAULT_LOOKUP_BIND, 0)
                    .maxConcurrentLookupRequests(5)
                    .serviceUrl(ApplicationConstants.BrokerServicesUrls.PULSAR_ADMIN_URL);

            if(brokersProperties.isSslEnabled()) {
                return client.useKeyStoreTls(true)
                        .tlsTrustStoreType("PKCS12")
                        .tlsKeyStorePassword("password")
                        .tlsTrustStorePath("/home/user/IdeaProjects/Kafka_Demo/certs/kafka.truststore.jks")
                        .tlsKeyStoreType("PKCS12")
                        .tlsKeyStorePath("/home/user/IdeaProjects/Kafka_Demo/certs/kafka.keystore.jks")
                        .tlsTrustStorePassword("password")
                        .build();
            }

            return client.build();
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

    @Bean
    DeadLetterPolicy deadLetterPolicy() {
        return DeadLetterPolicy.builder()
                .maxRedeliverCount(10)
                .deadLetterTopic(DLQ_PREFIX + brokersProperties.topicName())
                .build();
    }

}
