package com.example.kafka_demo.service;

import com.example.kafka_demo.config.configuration.properties.BrokersConfigProperties;
import com.example.kafka_demo.data.ThroughputData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "common.modes.producerMode", havingValue = "true")
public class ProducersService {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final DataTestUtilsService dataTestUtilsService;
    private final Producer<Object> pulsarProducer;
    private final BrokersConfigProperties brokersConfigProperties;

    @EventListener(ApplicationReadyEvent.class)
    void init() throws Exception {
        dataTestUtilsService.truncate();
        dataTestUtilsService.loadData(brokersConfigProperties.loadSize())
                .parallelStream()
                .forEach(message -> {
                    try {
                        message.setBrokerDomain(ThroughputData.BrokerDomain.PULSAR);
                        pulsarProducer.newMessage().value(message).send();
                        message.setBrokerDomain(ThroughputData.BrokerDomain.RABBIT);
                        rabbitTemplate.convertAndSend(brokersConfigProperties.topicName(), brokersConfigProperties.topicName(), message);
                        message.setBrokerDomain(ThroughputData.BrokerDomain.KAFKA);
                        kafkaTemplate.send(brokersConfigProperties.topicName(), objectMapper.writeValueAsBytes(message));
                    } catch (PulsarClientException ex) {
                        throw new RuntimeException("Error while sending message via Pulsar: " + ExceptionUtils.getMessage(ex));
                    } catch(JsonProcessingException ex) {
                        throw new RuntimeException("Error while deserializing message: " + ExceptionUtils.getMessage(ex));
                    }
                });
    }

}
