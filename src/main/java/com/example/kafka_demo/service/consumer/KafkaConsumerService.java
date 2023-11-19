package com.example.kafka_demo.service.consumer;

import com.example.kafka_demo.ApplicationException;
import com.example.kafka_demo.annotation.ConsumerMode;
import com.example.kafka_demo.data.AccumulationData;
import com.example.kafka_demo.data.ThroughputData;
import com.example.kafka_demo.service.DataTestUtilsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Bytes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

import static com.example.kafka_demo.utils.CommonAppUtils.logException;

@Service
@RequiredArgsConstructor
@ConsumerMode
public class KafkaConsumerService implements ConsumerSeekAware {

    private final ObjectMapper objectMapper;
    private final DataTestUtilsService dataTestUtilsService;

    @KafkaListener(topics = {"${common.topic.config.topic-name}"}, groupId = "testGroup", concurrency = "${common.topic.config.concurrency}")
    @Transactional(propagation = Propagation.REQUIRED)
    public void onMessage(ConsumerRecord<String, Bytes> data){
        try {
            long processingTimeMillis = System.currentTimeMillis() - data.timestamp();
            AccumulationData mainEntity = objectMapper.readValue(data.value().get(), AccumulationData.class);
            dataTestUtilsService.saveProcessingData(ThroughputData.BrokerDomain.KAFKA, processingTimeMillis, mainEntity);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            logException(e);
        } catch (IOException | KafkaException e) {
            throw new ApplicationException(e);
        }

    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        assignments.forEach((topic, action) -> callback.seekToEnd(topic.topic(), topic.partition()));
        ConsumerSeekAware.super.onPartitionsAssigned(assignments, callback);
    }

}
