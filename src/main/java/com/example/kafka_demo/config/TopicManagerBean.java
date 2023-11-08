package com.example.kafka_demo.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.common.policies.data.Policies;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.pulsar.core.PulsarTopic;

import static com.example.kafka_demo.ApplicationConstants.DEFAULT_CLUSTER_NAMESPACE;
import static org.springframework.pulsar.support.PulsarHeaders.TOPIC_NAME;

@Configuration
@RequiredArgsConstructor
public class TopicManagerBean {

    private final AppConfigurationProperties appConfigurationProperties;
    private final KafkaAdmin kafkaAdmin;
    private final PulsarAdmin pulsarAdmin;

    @EventListener(ApplicationReadyEvent.class)
    void initPartitions() throws PulsarAdminException {
        createKafkaTopic();
        Policies policies = pulsarAdmin.namespaces().getPolicies(DEFAULT_CLUSTER_NAMESPACE);
        policies.replication_clusters.clear();
        policies.replication_clusters.add(DEFAULT_CLUSTER_NAMESPACE);
    }

    private void createKafkaTopic() {
        NewTopic newTopic = new NewTopic(appConfigurationProperties.getBrokerConsumerConfigs().topicName(), appConfigurationProperties.getBrokerConsumerConfigs().numberOfPartitions(), appConfigurationProperties.getBrokerConsumerConfigs().replicationFactor());
        kafkaAdmin.createOrModifyTopics(newTopic);
    }

    @Bean
    public PulsarTopic pulsarTopic() {
        return PulsarTopic.builder(TOPIC_NAME)
                .numberOfPartitions(appConfigurationProperties.getBrokerConsumerConfigs().numberOfPartitions())
                .build();
    }

    @Bean
    TopicExchange exchange() {
        System.setProperty("spring.cloud.stream.bindings.output.producer.partition-count", String.valueOf(appConfigurationProperties.getBrokerConsumerConfigs().numberOfPartitions()));
        System.setProperty("spring.cloud.stream.bindings.output.producer.partition-key-expression", "headers['id']");

        return new TopicExchange(appConfigurationProperties.getBrokerConsumerConfigs().topicName());
    }

}
