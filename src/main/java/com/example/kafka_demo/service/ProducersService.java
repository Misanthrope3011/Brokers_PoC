package com.example.kafka_demo.service;

import com.example.kafka_demo.utils.RandomDataUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
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
@ConditionalOnProperty(value = "common.modes.producerMode", havingValue = "true")
public class ProducersService {

    private final RandomDataUtils randomDataUtils;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final DataTestUtilsService dataTestUtilsService;
    private final Producer<Object> pulsarProducer;
    private final ExecutorService executorService;

    @EventListener(ApplicationReadyEvent.class)
    void init() throws Exception {
        dataTestUtilsService.truncate();
        executorService.execute(() -> randomDataUtils.generateRandom(100L))
                .parallelStream()
                .forEach(message -> {
                    try {
                        pulsarProducer.newMessage().value(message).send();
                        rabbitTemplate.convertAndSend("my-queue1", "my-queue1", message);
                        kafkaTemplate.send("example-events", objectMapper.writeValueAsBytes(message));
                    } catch (PulsarClientException | JsonProcessingException ex) {
                        throw new RuntimeException(ex);
                    }
                });

        log.info("Load data finished");
    }

}
