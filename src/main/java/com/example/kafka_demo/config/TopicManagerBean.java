package com.example.kafka_demo.config;

import com.example.kafka_demo.ApplicationConstants;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.common.policies.data.Policies;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    private final RabbitAdmin rabbitAdmin;

    @PostConstruct
    void initPartitions() throws PulsarAdminException {
        createKafkaTopic();
        if (pulsarAdmin.topics().getLastMessageId(appConfigurationProperties.getBrokerConsumerConfigs().topicName()) != null) {
            pulsarAdmin.topics().resetCursor(appConfigurationProperties.getBrokerConsumerConfigs().topicName(), ApplicationConstants.SUBSCRIPTION_NAME, System.currentTimeMillis());
        }
        rabbitAdmin.purgeQueue(ApplicationConstants.EXCHANGE);
        Policies policies = pulsarAdmin.namespaces().getPolicies(DEFAULT_CLUSTER_NAMESPACE);
        policies.replication_clusters.clear();
        for (short i = 0; i < appConfigurationProperties.getBrokerConsumerConfigs().replicationFactor(); i++) {
            policies.replication_clusters.add(DEFAULT_CLUSTER_NAMESPACE.concat(String.valueOf(i)));
        }
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
        return new TopicExchange(appConfigurationProperties.getBrokerConsumerConfigs().topicName());
    }

}
