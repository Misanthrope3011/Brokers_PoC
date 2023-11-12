package com.example.kafka_demo.service;

import com.example.kafka_demo.data.AccumulationData;
import com.example.kafka_demo.data.ThroughputData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.utils.Bytes;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnExpression(value = "${common.modes.consumerMode} eq true and ${common.modes.partitioned} eq false ")
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final DataTestUtilsService dataTestUtilsService;

    @KafkaListener(topics = {"${common.topic.config.topic-name}"}, groupId = "testGroup", concurrency = "${common.topic.config.concurrency}")
    @Transactional(propagation = Propagation.REQUIRED)
    public void onMessage(ConsumerRecord<String, Bytes> data){
        try {
            long processingTimeMillis = System.currentTimeMillis() - data.timestamp();
            AccumulationData mainEntity = objectMapper.readValue(data.value().get(), AccumulationData.class);
            dataTestUtilsService.saveProcessingData(ThroughputData.BrokerDomain.KAFKA, processingTimeMillis, mainEntity);
        } catch (ConstraintViolationException | DataIntegrityViolationException | KafkaException e) {
            log.error(ExceptionUtils.getMessage(e));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
