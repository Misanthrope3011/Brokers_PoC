package com.example.kafka_demo.config.rabbitmq;

import com.example.kafka_demo.annotation.StreamsMode;
import com.example.kafka_demo.config.configuration.properties.BrokersConfigProperties;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
@Slf4j
@StreamsMode
public class PartitionedConfig {

    private static final String PARTITIONED_SUFFIX = "_PARTITIONED";
    private final DataTestUtilsService dataTestUtilsService;
    private final RandomDataUtils randomDataUtils;
    private final BrokersConfigProperties brokersConfigProperties;

    @Value("${spring.cloud.stream.default-binder}")
    private String binder;

    @Bean
    public Supplier<Flux<Message<AccumulationData>>> senderMethod() {
        List<AccumulationData> entity = randomDataUtils.generateRandomData(1L);
        AtomicInteger counter = new AtomicInteger();
        entity.forEach(var -> var.setBrokerDomain(ThroughputData.BrokerDomain.valueOf(binder.toUpperCase().concat(PARTITIONED_SUFFIX))));

        return () -> Flux.interval(Duration.ofMillis(10))
                .onBackpressureLatest()
                .map(tick -> {
                    counter.incrementAndGet();
            return MessageBuilder
                    .withPayload(entity.get(0))
                    .setHeader("custom_timestamp", System.currentTimeMillis())
                    .build();
        }).takeWhile((message) -> counter.get() < brokersConfigProperties.loadSize());
    }

    @Bean
    @SuppressWarnings("ConstantConditions")
    public Consumer<Message<AccumulationData>> consumerMethod() {
        return (value) -> {
            long processingTime = System.currentTimeMillis() - (Long) value.getHeaders().get("custom_timestamp");

            dataTestUtilsService.saveThroughPutData(new ThroughputData(ThroughputData.BrokerDomain.valueOf(binder.toUpperCase().concat(PARTITIONED_SUFFIX)), processingTime));
        };
    }

}
