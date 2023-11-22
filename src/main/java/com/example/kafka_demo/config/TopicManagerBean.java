package com.example.kafka_demo.config;

import com.example.kafka_demo.ApplicationConstants;
import com.example.kafka_demo.annotation.ConsumerMode;
import com.example.kafka_demo.annotation.ProducerMode;
import jakarta.annotation.PostConstruct;
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

import static com.example.kafka_demo.ApplicationConstants.*;

@Configuration
@RequiredArgsConstructor
public class TopicManagerBean {

    private final AppConfigurationProperties appConfigurationProperties;
    private final KafkaAdmin kafkaAdmin;
    private final PulsarAdmin pulsarAdmin;
    private final RabbitAdmin rabbitAdmin;


    @PostConstruct
    @ProducerMode
    @ConsumerMode
    public void pulsarTopic() throws PulsarAdminException {
        String FULL_TOPIC_NAME = FULL_TOPIC_PREFIX + "/" + appConfigurationProperties.getBrokerConsumerConfigs().topicName();
        if(pulsarAdmin.topics().getPartitionedTopicMetadata(FULL_TOPIC_NAME) != null && pulsarAdmin.topics().getPartitionedTopicMetadata(FULL_TOPIC_NAME).partitions > 0) {
            pulsarAdmin.topics().updatePartitionedTopic(appConfigurationProperties.getBrokerConsumerConfigs().topicName(), appConfigurationProperties.getBrokerConsumerConfigs().numberOfPartitions());
        } else if(pulsarAdmin.topics().getList(DEFAULT_NAMESPACE).contains(FULL_TOPIC_NAME)) {
            pulsarAdmin.topics().delete(appConfigurationProperties.getBrokerConsumerConfigs().topicName(), true);
            pulsarAdmin.topics().createPartitionedTopic(appConfigurationProperties.getBrokerConsumerConfigs().topicName(), appConfigurationProperties.getBrokerConsumerConfigs().numberOfPartitions());
        }
    }

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
    TopicExchange exchange() {
        return new TopicExchange(appConfigurationProperties.getBrokerConsumerConfigs().topicName());
    }

}
