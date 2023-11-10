package com.example.kafka_demo.config.rabbitmq;

import com.example.kafka_demo.data.AccumulationData;
import com.example.kafka_demo.data.ThroughputData;
import com.example.kafka_demo.service.DataTestUtilsService;
import com.example.kafka_demo.utils.RandomDataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class RabbitMQPartitionedConfig {

    private final DataTestUtilsService dataTestUtilsService;
    private final RandomDataUtils randomDataUtils;

    @Bean
    @SuppressWarnings("unchecked")
    public Supplier<Flux<Message<AccumulationData>>> senderMethod(){
        List<AccumulationData> entity = randomDataUtils.generateRandomData(1L);
        entity.forEach(var -> var.setReceiveTime(System.currentTimeMillis()));

        return () -> Flux.interval(Duration.ofMillis(1)).map(tick -> {

             return MessageBuilder
                    .withPayload(entity.get(0))
                    .build();
        });
    }

    @Bean
    public Consumer<Message<AccumulationData>> consumerMethod() {
        return (value) -> {
            dataTestUtilsService.saveThroughtPutData(new ThroughputData(ThroughputData.BrokerDomain.RABBITMQ_PARTITIONED, System.currentTimeMillis() - value.getHeaders().getTimestamp()));
        };
    }

}
