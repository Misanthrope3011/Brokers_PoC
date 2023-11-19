package com.example.kafka_demo.service.consumer;

import com.example.kafka_demo.ApplicationException;
import com.example.kafka_demo.annotation.ConsumerMode;
import com.example.kafka_demo.data.AccumulationData;
import com.example.kafka_demo.data.ThroughputData;
import com.example.kafka_demo.service.DataTestUtilsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
@ConsumerMode
public class RabbitMQConsumerService implements MessageListener {

    private final DataTestUtilsService dataTestUtilsService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void onMessage(Message message) {
        try {
            long processingTimeMillis = System.currentTimeMillis() - message.getMessageProperties().getTimestamp().getTime();
            var mainEntity = objectMapper.readValue(message.getBody(), AccumulationData.class);
            dataTestUtilsService.saveProcessingData(ThroughputData.BrokerDomain.RABBIT, processingTimeMillis, mainEntity);
        }  catch (ConstraintViolationException | DataIntegrityViolationException e) {
            log.error(ExceptionUtils.getMessage(e));
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

}