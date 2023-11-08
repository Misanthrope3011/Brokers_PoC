package com.example.kafka_demo.service;

import com.example.kafka_demo.data.AccumulationData;
import com.example.kafka_demo.data.ThroughputData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.utils.Bytes;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "common.modes.consumerMode", havingValue = "true")
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final DataTestUtilsService dataTestUtilsService;

    @KafkaListener(topics = {"${common.topic.config.topic-name}"}, groupId = "testGroup", concurrency = "${common.topic.config.concurrency}")
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void onMessage(ConsumerRecord<String, Bytes> data){
        try {
            long processingTimeMillis = System.currentTimeMillis() - data.timestamp();
            AccumulationData mainEntity = objectMapper.readValue(data.value().get(), AccumulationData.class);
            var throughputData = new ThroughputData(ThroughputData.BrokerDomain.KAFKA, processingTimeMillis);
            dataTestUtilsService.saveThroughtPutData(throughputData);
            dataTestUtilsService.saveOuterEntity(mainEntity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
