package com.example.kafka_demo.config.rabbitmq;

import com.example.kafka_demo.data.AccumulationData;
import com.example.kafka_demo.data.ThroughputData;
import com.example.kafka_demo.service.DataTestUtilsService;
import com.example.kafka_demo.utils.RandomDataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class PartitionedConfig {

    private final DataTestUtilsService dataTestUtilsService;
    private final RandomDataUtils randomDataUtils;
    private static final String PARTITIONED_PREFIX = "_partitioned";

    @Value("${spring.cloud.stream.default-binder}")
    private String binder;

    @Bean
    @SuppressWarnings("unchecked")
    public Supplier<Flux<Message<AccumulationData>>> senderMethod(){
        List<AccumulationData> entity = randomDataUtils.generateRandomData(1L);
        entity.forEach(var -> var.setBrokerDomain(ThroughputData.BrokerDomain.valueOf(binder.toUpperCase().concat(PARTITIONED_PREFIX.toUpperCase()))));

        return () -> Flux.interval(Duration.ofMillis(1)).map(tick -> {
             return MessageBuilder
                    .withPayload(entity.get(0))
                    .build();
        });
    }

    @Bean
    public Consumer<Message<AccumulationData>> consumerMethod() {
        return (value) -> {
            dataTestUtilsService.saveThroughtPutData(new ThroughputData(value.getPayload().getBrokerDomain(), System.currentTimeMillis() - (Long) value.getHeaders().get("kafka_receivedTimestamp")));
        };
    }

}
