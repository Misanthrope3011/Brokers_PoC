package com.example.kafka_demo.config;

import com.example.kafka_demo.service.RabbitMQConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.pulsar.annotation.EnablePulsar;

@Configuration
@EnableKafka
@EnablePulsar
@EnableRabbit
@RequiredArgsConstructor
public class BrokerConsumerConfigs {

    private final RabbitMQConsumerService rabbitMQConsumerService;


    @Bean
    public ConnectionFactory connectionFactory() {
        var connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        connectionFactory.setUsername("myuser");
        connectionFactory.setPassword("mypassword");
        return connectionFactory;
    }

    @Bean
    public Queue queue() {
        return new Queue("my-queue1");
    }

    @Bean
    public SimpleMessageListenerContainer listenerContainer() {
        var container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(queue());
        container.setMessageListener(rabbitMQConsumerService);
        return container;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        return factory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setDefaultReceiveQueue("my-queue1");
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange("my-queue1");
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("my-queue1");
    }

}
