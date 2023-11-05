package com.example.kafka_demo.repository;

import com.example.kafka_demo.data.ThrougputData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThroughtputDataRepository extends JpaRepository<ThrougputData, Long> {
}

