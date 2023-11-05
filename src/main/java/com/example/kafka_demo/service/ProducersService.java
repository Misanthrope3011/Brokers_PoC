package com.example.kafka_demo.service;

import com.example.kafka_demo.utils.RandomDataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@ConditionalOnProperty(value = "producerMode", havingValue = "true")
public class ProducersService {

    private final RandomDataUtils randomDataUtils;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final DataTestUtilsService dataTestUtilsService;
    private final Producer<Object> pulsarProducer;

    @EventListener(ApplicationReadyEvent.class)
    void init() {
        dataTestUtilsService.truncate();
        randomDataUtils.generateRandom(100L)
                .parallelStream()
                .forEach(message -> {
                    /*                    try {*/
                    try {
                        pulsarProducer.newMessage().value(message).send();
                    } catch (PulsarClientException e) {
                        throw new RuntimeException(e);
                    }
                    rabbitTemplate.convertAndSend("my-queue1", "my-queue1", message);

//                        kafkaTemplate.send("example-events", objectMapper.writeValueAsBytes(message));
                    /*                    } *//*catch (PulsarClientException ex) {
                        throw new RuntimeException(ex);
                    }*/
                });
        log.info("Load data finished");
    }


}
