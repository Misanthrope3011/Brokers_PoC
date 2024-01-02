package com.example.kafka_demo.config.rabbitmq;

import com.example.kafka_demo.ApplicationConstants;
import com.example.kafka_demo.annotation.ConsumerMode;
import com.example.kafka_demo.config.AppConfigurationProperties;
import com.example.kafka_demo.service.consumer.RabbitMQConsumerService;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.pulsar.annotation.EnablePulsar;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Configuration
@EnableKafka
@EnablePulsar
@EnableRabbit
@RequiredArgsConstructor
public class RabbitMQBrokerConfig {

    private final AppConfigurationProperties appConfigProperties;

    @Bean
    public ConnectionFactory connectionFactory() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {
        var rabbitPort = appConfigProperties.getBrokerConsumerConfigs().isSslEnabled() ? ApplicationConstants.RABBITMQ_SECURE_PORT : ApplicationConstants.RABBITMQ_PORT;
        var connectionFactory = new CachingConnectionFactory(appConfigProperties.getBrokerConsumerConfigs().hostName(), rabbitPort);
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        connectionFactory.setUsername(appConfigProperties.getCredentialsConfig().rabbitmq().username());
        connectionFactory.setPassword(appConfigProperties.getCredentialsConfig().rabbitmq().password());
        if(appConfigProperties.getBrokerConsumerConfigs().isSslEnabled()) {
            char[] keyPassphrase = "datahub".toCharArray();
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream("/home/sebastian/IdeaProjects/kafka-ssl-compose/secrets/broker.keystore.jks"), keyPassphrase);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, keyPassphrase);

            char[] trustPassphrase = "datahub".toCharArray();
            KeyStore tks = KeyStore.getInstance("PKCS12");
            tks.load(new FileInputStream("/home/sebastian/IdeaProjects/kafka-ssl-compose/secrets/broker.truststore.jks"), trustPassphrase);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(tks);

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            connectionFactory.getRabbitConnectionFactory().useSslProtocol(sslContext);
        }

        return connectionFactory;
    }

    @Bean
    public Queue queue() {
        return new Queue(appConfigProperties.getBrokerConsumerConfigs().topicName());
    }

    @Bean
    @ConsumerMode
    public SimpleMessageListenerContainer listenerContainer(RabbitMQConsumerService rabbitMQConsumerService) throws NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException {
        var container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(queue());
        container.setConcurrency(String.valueOf(appConfigProperties.getBrokerConsumerConfigs().concurrency()));
        container.setMessageListener(rabbitMQConsumerService);
        return container;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() throws NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        return factory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate() throws NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException {
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
    public RabbitAdmin rabbitAdmin() throws NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException {
        return new RabbitAdmin(connectionFactory());
    }

}
