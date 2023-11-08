package com.example.kafka_demo.service;

import com.example.kafka_demo.data.AccumulationData;
import com.example.kafka_demo.data.ThroughputData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "common.modes.consumerMode", havingValue = "true")
public class RabbitMQConsumerService implements MessageListener {

    private final DataTestUtilsService dataTestUtilsService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void onMessage(Message message) {
        try {
            long processingTimeMillis = System.currentTimeMillis() - message.getMessageProperties().getTimestamp().getTime();
            var mainEntity = objectMapper.readValue(message.getBody(), AccumulationData.class);
            var throughputData = new ThroughputData(ThroughputData.BrokerDomain.RABBITMQ, processingTimeMillis);
            dataTestUtilsService.saveThroughtPutData(throughputData);
            dataTestUtilsService.saveOuterEntity(mainEntity);
        } catch (IOException e) {
            log.error("Exception during deserializing message " + ExceptionUtils.getMessage(e));
        }
    }

}