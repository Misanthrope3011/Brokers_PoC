package com.example.kafka_demo.repository;

import com.example.kafka_demo.data.BrokerConfigurationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BrokerConfigurationRepository extends JpaRepository<BrokerConfigurationData, Long> {

    @Query("SELECT data from BrokerConfigurationData data WHERE data.numPartitions = ?1 AND data.isCloud = ?2 AND data.loadSize = ?3 AND data.numberOfConsumers = ?4 AND data.messageSizeBytes = ?5 AND data.isSslEnabled = ?6")
    Optional<BrokerConfigurationData> isCurrentConfigExists(long numPartitions, boolean isCloud, long loadSize, short numberOfConsumers, long messageSizeBytes, boolean isSslEnabled);

}

