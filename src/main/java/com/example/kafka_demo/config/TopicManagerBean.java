package com.example.kafka_demo.config;

import com.example.kafka_demo.ApplicationConstants;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.common.policies.data.Policies;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.pulsar.core.PulsarTopic;

import static com.example.kafka_demo.ApplicationConstants.DEFAULT_CLUSTER_NAMESPACE;
import static com.example.kafka_demo.ApplicationConstants.InvocationPriority;
import static org.springframework.pulsar.support.PulsarHeaders.TOPIC_NAME;

@Configuration
@RequiredArgsConstructor
public class TopicManagerBean {

    private final AppConfigurationProperties appConfigurationProperties;
    private final KafkaAdmin kafkaAdmin;
    private final PulsarAdmin pulsarAdmin;
    private final RabbitAdmin rabbitAdmin;

    @EventListener(ApplicationReadyEvent.class)
    @Order(InvocationPriority.HIGHEST)
    void initPartitions() throws PulsarAdminException {
        createKafkaTopic();
        String TOPIC_NAME = appConfigurationProperties.getBrokerConsumerConfigs().topicName();
        if (pulsarAdmin.topics().getList("public/default").contains(TOPIC_NAME) && pulsarAdmin.topics().getLastMessageId(TOPIC_NAME) != null) {
            pulsarAdmin.topics().resetCursor(TOPIC_NAME, ApplicationConstants.SUBSCRIPTION_NAME, System.currentTimeMillis());
        }
        if(rabbitAdmin.getQueueInfo(TOPIC_NAME) != null) {
            rabbitAdmin.purgeQueue(TOPIC_NAME);
        }
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
