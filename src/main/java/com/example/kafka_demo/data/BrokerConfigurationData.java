package com.example.kafka_demo.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "BROKER_CONFIGURATION_DATA")
@NoArgsConstructor
public class BrokerConfigurationData {

    @Id
    @Column(name = "BCD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BDC_NUM_PARTITIONS")
    private long numPartitions;

    @Column(name = "BDC_LOAD_SIZE")
    private long loadSize;

    @Column(name = "BCD_IS_CLOUD")
    private boolean isCloud;

    @Column(name = "BCD_CONCURRENCY")
    private long numberOfConsumers;

    @Column(name = "BDC_MESSAGE_SIZE_BYTES")
    private long messageSizeBytes;

    @Column(name = "BDC_IS_SSL_ENABLED")
    private boolean isSslEnabled;

    public BrokerConfigurationData(long numPartitions, long loadSize, boolean isCloud, long numberOfConsumers, long messageSizeBytes, boolean isSslEnabled) {
        this.numPartitions = numPartitions;
        this.loadSize = loadSize;
        this.isCloud = isCloud;
        this.numberOfConsumers = numberOfConsumers;
        this.messageSizeBytes = messageSizeBytes;
        this.isSslEnabled = isSslEnabled;
    }

}
