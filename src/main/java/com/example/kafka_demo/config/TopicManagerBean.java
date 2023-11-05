package com.example.kafka_demo.config;

import com.example.kafka_demo.config.properties.BrokersConfig;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.common.policies.data.Policies;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.pulsar.core.PulsarTopic;

import static org.springframework.pulsar.support.PulsarHeaders.TOPIC_NAME;

@Configuration
@RequiredArgsConstructor
public class TopicManagerBean {

    private final BrokersConfig brokersConfig;
    private final KafkaAdmin kafkaAdmin;
    private final PulsarAdmin pulsarAdmin;
    public static final String DEFAULT_CLUSTER_NAMESPACE = "public/default";

    @EventListener(ApplicationReadyEvent.class)
    void initPartitions() throws PulsarAdminException {
        createKafkaTopic();
//        Policies policies = pulsarAdmin.namespaces().getPolicies(DEFAULT_CLUSTER_NAMESPACE);
//        policies.replication_clusters.clear();
//        policies.replication_clusters.add(DEFAULT_CLUSTER_NAMESPACE);

    }

    private void createKafkaTopic() {
        NewTopic newTopic = new NewTopic(brokersConfig.topicName(), brokersConfig.numberOfPartitions(), brokersConfig.replicationFactor());
        kafkaAdmin.createOrModifyTopics(newTopic);
    }

    @Bean
    public PulsarTopic pulsarTopic() {
        return PulsarTopic.builder(TOPIC_NAME)
                .numberOfPartitions(5)
                .build();
    }

}
