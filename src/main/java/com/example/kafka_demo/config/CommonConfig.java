package com.example.kafka_demo.config;

import com.example.kafka_demo.ApplicationConstants;
import com.example.kafka_demo.data.BrokerConfigurationData;
import com.example.kafka_demo.repository.BrokerConfigurationRepository;
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
                .build();
    }

    @Bean
    public BrokerConfigurationData getBrokerConfig() {
        Optional<BrokerConfigurationData> brokerConfigurationData = brokerConfigurationRepository.findByNumPartitionsAndIsCloudAndLoadSizeAndNumberOfConsumers(appConfigurationProperties.getBrokerConsumerConfigs().numberOfPartitions(),
                isPartitioned, appConfigurationProperties.getBrokerConsumerConfigs().loadSize(), appConfigurationProperties.getBrokerConsumerConfigs().concurrency());

        return brokerConfigurationData.orElseGet(() -> brokerConfigurationRepository.save(new BrokerConfigurationData(appConfigurationProperties.getBrokerConsumerConfigs().numberOfPartitions(),
                appConfigurationProperties.getBrokerConsumerConfigs().loadSize(), isPartitioned, appConfigurationProperties.getBrokerConsumerConfigs().concurrency())));

    }

}
