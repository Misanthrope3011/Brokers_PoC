package com.example.kafka_demo.service;

import com.example.kafka_demo.data.MainEntity;
import com.example.kafka_demo.data.ThroughputData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;
import org.apache.pulsar.shade.com.google.common.primitives.Bytes;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "common.modes.consumerMode", havingValue = "true")
public class PulsarConsumerService {

    private final ObjectMapper objectMapper;
    private final DataTestUtilsService dataTestUtilsService;

    @PulsarListener(
            subscriptionName = "string-topic-subscription",
            topics = "${common.topic.config.topic-name}",
            subscriptionType = SubscriptionType.Shared,
            schemaType = SchemaType.JSON
    )
    @Transactional(propagation = Propagation.REQUIRED)
    public void stringTopicListener(Consumer<Bytes> consumer, Message<Bytes> msg) {
        try {
            long processingTimeMillis = System.currentTimeMillis() - msg.getPublishTime();
            MainEntity entity = objectMapper.readValue(msg.getData(), MainEntity.class);
            var througputData = new ThroughputData(ThroughputData.BrokerDomain.PULSAR, processingTimeMillis);
            dataTestUtilsService.saveThroughtPutData(througputData);
            dataTestUtilsService.saveOuterEntity(entity);
        } catch (IOException ex) {
            log.error("Exception during deserializing message : ".concat(ExceptionUtils.getMessage(ex)));
        }
    }

}

