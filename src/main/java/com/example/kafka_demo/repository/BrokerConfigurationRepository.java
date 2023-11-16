package com.example.kafka_demo.repository;

import com.example.kafka_demo.data.BrokerConfigurationData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrokerConfigurationRepository extends JpaRepository<BrokerConfigurationData, Long> {

    Optional<BrokerConfigurationData> findByNumPartitionsAndIsCloudAndLoadSizeAndNumberOfConsumers(long numPartitions, boolean isCloud, long loadSize, short numberOfConsumers);

}

