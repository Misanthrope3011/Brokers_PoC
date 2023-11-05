package com.example.kafka_demo.service;

import com.example.kafka_demo.data.MainEntity;
import com.example.kafka_demo.data.ThrougputData;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.pulsar.shade.com.google.gson.JsonObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "consumerMode", havingValue = "true")
public class RabbitMQConsumerService implements MessageListener {

    private final DataTestUtilsService dataTestUtilsService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void onMessage(Message message) {
        try {
            long processingTimeMillis = System.currentTimeMillis() - message.getMessageProperties().getTimestamp().getTime();
            var mainEntity = objectMapper.readValue(message.getBody(), MainEntity.class);
            var througputData = new ThrougputData(ThrougputData.BrokerDomain.RABBITMQ, processingTimeMillis);
            dataTestUtilsService.saveThroughtPutData(througputData);
            dataTestUtilsService.saveOuterEntity(mainEntity);
        } catch (IOException e) {
            log.error("Exception during deserializing message " + ExceptionUtils.getMessage(e));
        }
    }

}