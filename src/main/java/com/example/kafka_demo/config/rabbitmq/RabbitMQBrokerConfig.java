package com.example.kafka_demo.config.rabbitmq;

import com.example.kafka_demo.ApplicationConstants;
import com.example.kafka_demo.config.AppConfigurationProperties;
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
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.pulsar.annotation.EnablePulsar;

@Configuration
@EnableKafka
@EnablePulsar
@EnableRabbit
@RequiredArgsConstructor
public class RabbitMQBrokerConfig {

    private final AppConfigurationProperties appConfigProperties;

    @Bean
    public ConnectionFactory connectionFactory() {
        var connectionFactory = new CachingConnectionFactory(appConfigProperties.getBrokerConsumerConfigs().hostName());
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        connectionFactory.setUsername(appConfigProperties.getCredentialsConfig().rabbitmq().username());
        connectionFactory.setPassword(appConfigProperties.getCredentialsConfig().rabbitmq().password());
        return connectionFactory;
    }

    @Bean
    public Queue queue() {
        return new Queue(ApplicationConstants.EXCHANGE);
    }

    @Bean
    @ConditionalOnExpression(value = "${common.modes.consumerMode} eq true and ${common.modes.partitioned} eq false ")
    public SimpleMessageListenerContainer listenerContainer(RabbitMQConsumerService rabbitMQConsumerService) {
        var container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(queue());
        container.setConcurrency(String.valueOf(appConfigProperties.getBrokerConsumerConfigs().concurrency()));
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
        template.setDefaultReceiveQueue(appConfigProperties.getBrokerConsumerConfigs().topicName());
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(appConfigProperties.getBrokerConsumerConfigs().topicName());
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

}
