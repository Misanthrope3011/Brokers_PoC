package com.example.kafka_demo.config;

import com.example.kafka_demo.ApplicationConstants;
import com.example.kafka_demo.config.configuration.properties.RandomDataProperties;
import com.example.kafka_demo.data.BrokerConfigurationData;
import com.example.kafka_demo.repository.BrokerConfigurationRepository;
import com.example.kafka_demo.service.DataTestUtilsService;
import lombok.RequiredArgsConstructor;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class CommonConfig {

    private final AppConfigurationProperties appConfigurationProperties;
    private final BrokerConfigurationRepository brokerConfigurationRepository;

    @Value(value = "${common.modes.partitioned}")
    private boolean isPartitioned;

    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }

    @Bean
    public PulsarAdmin pulsarAdmin() throws PulsarClientException {
        return PulsarAdmin.builder()
                .serviceHttpUrl(ApplicationConstants.BrokerServicesUrls.PULSAR_ADMIN_URL)
                .allowTlsInsecureConnection(true)
                .useKeyStoreTls(true)
                .tlsTrustStoreType("PKCS12")
                .tlsKeyStorePassword("password")
                .tlsTrustStorePath("/home/user/IdeaProjects/Kafka_Demo/certs/kafka.truststore.jks")
                .tlsKeyStoreType("PKCS12")
                .tlsKeyStorePath("/home/user/IdeaProjects/Kafka_Demo/certs/kafka.keystore.jks")
                .tlsTrustStorePassword("password")
                .build();
    }

    @Bean
    public BrokerConfigurationData getBrokerConfig() {
        RandomDataProperties randomDataProperties = appConfigurationProperties.getRandomDataProperties();
        int messageSize = randomDataProperties.subEntityArraySize() * (randomDataProperties.descSizeBytes() + randomDataProperties.nameSizeBytes()) + randomDataProperties.imageSizeBytes();
        Optional<BrokerConfigurationData> brokerConfigurationData = brokerConfigurationRepository.isCurrentConfigExists(appConfigurationProperties.getBrokerConsumerConfigs().numberOfPartitions(),
                isPartitioned, appConfigurationProperties.getBrokerConsumerConfigs().loadSize(), appConfigurationProperties.getBrokerConsumerConfigs().concurrency(), messageSize, appConfigurationProperties.getBrokerConsumerConfigs().isSslEnabled());

        return brokerConfigurationData.orElseGet(() -> brokerConfigurationRepository.save(new BrokerConfigurationData(appConfigurationProperties.getBrokerConsumerConfigs().numberOfPartitions(),
                appConfigurationProperties.getBrokerConsumerConfigs().loadSize(), isPartitioned, appConfigurationProperties.getBrokerConsumerConfigs().concurrency(), messageSize, appConfigurationProperties.getBrokerConsumerConfigs().isSslEnabled())));

    }

}
