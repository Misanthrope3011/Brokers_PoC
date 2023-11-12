package com.example.kafka_demo.service;

import com.example.kafka_demo.ApplicationConstants;
import com.example.kafka_demo.data.AccumulationData;
import com.example.kafka_demo.data.ThroughputData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;
import org.apache.pulsar.shade.com.google.common.primitives.Bytes;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnExpression(value = "${common.modes.consumerMode} eq true and ${common.modes.partitioned} eq false ")
public class PulsarConsumerService {

    private final ObjectMapper objectMapper;
    private final DataTestUtilsService dataTestUtilsService;

    @PulsarListener(
            subscriptionName = ApplicationConstants.SUBSCRIPTION_NAME,
            topics = "${common.topic.config.topic-name}",
            subscriptionType = SubscriptionType.Shared,
            schemaType = SchemaType.JSON,
            concurrency =  "${common.topic.config.concurrency}"
    )
    @Transactional(propagation = Propagation.REQUIRED)
    public void stringTopicListener(Consumer<Bytes> consumer, Message<Bytes> msg) {
        try {
            long processingTimeMillis = System.currentTimeMillis() - msg.getPublishTime();
            AccumulationData entity = objectMapper.readValue(msg.getData(), AccumulationData.class);
            dataTestUtilsService.saveProcessingData(ThroughputData.BrokerDomain.PULSAR, processingTimeMillis, entity);
        }  catch (ConstraintViolationException | DataIntegrityViolationException e) {
            log.error(ExceptionUtils.getMessage(e));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

