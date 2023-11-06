package com.example.kafka_demo.config;

import com.example.kafka_demo.config.properties.BrokersConfigProperties;
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

import static org.springframework.pulsar.support.PulsarHeaders.TOPIC_NAME;

@Configuration
@RequiredArgsConstructor
public class TopicManagerBean {

    private final BrokersConfigProperties brokersConfig;
    private final KafkaAdmin kafkaAdmin;
    private final PulsarAdmin pulsarAdmin;
    public static final String DEFAULT_CLUSTER_NAMESPACE = "public/default";

    @EventListener(ApplicationReadyEvent.class)
    void initPartitions() throws PulsarAdminException {
        createKafkaTopic();
            Policies policies = pulsarAdmin.namespaces().getPolicies(DEFAULT_CLUSTER_NAMESPACE);
            policies.replication_clusters.clear();
            policies.replication_clusters.add(DEFAULT_CLUSTER_NAMESPACE);

    }

    private void createKafkaTopic() {
        NewTopic newTopic = new NewTopic(brokersConfig.topicName(), brokersConfig.numberOfPartitions(), brokersConfig.replicationFactor());
        kafkaAdmin.createOrModifyTopics(newTopic);
    }

    @Bean
    public PulsarTopic pulsarTopic() {
        return PulsarTopic.builder(TOPIC_NAME)
                .numberOfPartitions(brokersConfig.numberOfPartitions())
                .build();
    }

    @Bean
    TopicExchange exchange() {
        System.setProperty("spring.cloud.stream.bindings.output.producer.partition-count", String.valueOf(brokersConfig.numberOfPartitions()));
        System.setProperty("spring.cloud.stream.bindings.output.producer.partition-key-expression", "headers['id']");

        return new TopicExchange(brokersConfig.topicName());
    }

}