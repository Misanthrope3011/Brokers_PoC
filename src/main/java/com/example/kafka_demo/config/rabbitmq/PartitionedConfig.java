package com.example.kafka_demo.config.rabbitmq;

import com.example.kafka_demo.data.AccumulationData;
import com.example.kafka_demo.data.ThroughputData;
import com.example.kafka_demo.service.DataTestUtilsService;
import com.example.kafka_demo.utils.RandomDataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = {"common.modes.partitioned"})
public class PartitionedConfig {

    private static final String PARTITIONED_SUFFIX = "_PARTITIONED";
    private final DataTestUtilsService dataTestUtilsService;
    private final RandomDataUtils randomDataUtils;

    @Value("${spring.cloud.stream.default-binder}")
    private String binder;

    @Bean
    @SuppressWarnings("unchecked")
    public Supplier<Flux<Message<AccumulationData>>> senderMethod() {
        List<AccumulationData> entity = randomDataUtils.generateRandomData(1L);
        entity.forEach(var -> var.setBrokerDomain(ThroughputData.BrokerDomain.valueOf(binder.toUpperCase().concat(PARTITIONED_SUFFIX.toUpperCase()))));

        return () -> Flux.interval(Duration.ofMillis(1)).map(tick -> {
            return MessageBuilder
                    .withPayload(entity.get(0))
                    .build();
        });
    }

    @Bean
    public Consumer<Message<AccumulationData>> consumerMethod() {
        return (value) -> {
            long processingTime = System.currentTimeMillis();
            switch (binder) {
                case "kafka" -> {
                    processingTime -= processingTime - (Long) value.getHeaders().get("kafka_receivedTimestamp");
                }
                case "pulsar" -> {
                    processingTime -= (Long) value.getHeaders().get("pulsar_message_publish_time");
                }
                case "rabbit" -> {
                    processingTime -= value.getHeaders().getTimestamp();
                }
            }
            dataTestUtilsService.saveThroughtPutData(new ThroughputData(ThroughputData.BrokerDomain.valueOf(binder.toUpperCase().concat(PARTITIONED_SUFFIX)), processingTime));
        };
    }

}
