package com.example.kafka_demo.service;

import com.example.kafka_demo.data.MainEntity;
import com.example.kafka_demo.data.ThrougputData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.utils.Bytes;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "consumerMode", havingValue = "true")
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final DataTestUtilsService dataTestUtilsService;

    @KafkaListener(topics = {"example-events"}, groupId = "xddd")
    @Transactional(propagation = Propagation.REQUIRED)
    public void onMessage(ConsumerRecord<String, Bytes> data){
        try {
            long processingTimeMillis = System.currentTimeMillis() - data.timestamp();
            MainEntity mainEntity = objectMapper.readValue(data.value().get(), MainEntity.class);
            var througputData = new ThrougputData(ThrougputData.BrokerDomain.KAFKA, processingTimeMillis);
            dataTestUtilsService.saveThroughtPutData(througputData);
            dataTestUtilsService.saveOuterEntity(mainEntity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
