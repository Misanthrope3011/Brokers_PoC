package com.example.kafka_demo.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "THROUGHPUT_DATA")
@NoArgsConstructor
public class ThroughputData {

    @Id
    @Column(name = "TRD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "TRD_BROKER_DOMAIN")
    private BrokerDomain brokerDomain;

    @Column(name = "TRD_PROCESSING_TIME")
    private Long processingTimeMillis;

    @Column(name = "TRD_AUDIT_CD")
    private LocalDateTime localDateTime = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "TRD_BCD_ID")
    private BrokerConfigurationData brokerConfigurationData;

    @Getter
    public enum BrokerDomain {
        KAFKA("kafka"),
        RABBIT_PARTITIONED("rabbitmq_partitioned"),
        KAFKA_PARTITIONED("kafka_partitioned"),
        PULSAR_PARTITIONED("pulsar_partitioned"),
        PULSAR("pulsar"),
        RABBIT("rabbitmq");
        private final String name;

        BrokerDomain(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public ThroughputData(BrokerDomain brokerDomain, Long processingTimeMillis) {
        this.brokerDomain = brokerDomain;
        this.processingTimeMillis = processingTimeMillis;
    }

}
