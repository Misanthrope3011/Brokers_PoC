package com.example.kafka_demo.service.producer;

import com.example.kafka_demo.ApplicationException;
import com.example.kafka_demo.annotation.ProducerMode;
import com.example.kafka_demo.config.configuration.properties.BrokersConfigProperties;
import com.example.kafka_demo.service.DataTestUtilsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.example.kafka_demo.ApplicationConstants.InvocationPriority;
import static com.example.kafka_demo.utils.CommonAppUtils.logException;


@Service
@Slf4j
@RequiredArgsConstructor
@ProducerMode
public class ProducersService {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final DataTestUtilsService dataTestUtilsService;
    private final Producer<Object> pulsarProducer;
    private final BrokersConfigProperties brokersConfigProperties;

    @EventListener(ApplicationReadyEvent.class)
    @Order(InvocationPriority.LOW)
    void init() throws Exception {
        dataTestUtilsService.loadData(brokersConfigProperties.loadSize())
                .parallelStream()
                .forEach(message -> {
                    try {
                        pulsarProducer.newMessage().value(message).send();
                        rabbitTemplate.convertAndSend(brokersConfigProperties.topicName(), brokersConfigProperties.topicName(), message);
                        kafkaTemplate.send(brokersConfigProperties.topicName(), objectMapper.writeValueAsBytes(message));
                    } catch (PulsarClientException ex) {
                        throw new ApplicationException("Error while sending message via Pulsar: " + ExceptionUtils.getMessage(ex));
                    } catch(JsonProcessingException ex) {
                        logException(ex);
                    }
                });
    }

}
