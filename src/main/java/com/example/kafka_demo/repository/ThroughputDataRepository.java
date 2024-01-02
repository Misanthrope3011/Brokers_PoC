package com.example.kafka_demo.repository;

import com.example.kafka_demo.data.ThroughputData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThroughputDataRepository extends JpaRepository<ThroughputData, Long> {
}

