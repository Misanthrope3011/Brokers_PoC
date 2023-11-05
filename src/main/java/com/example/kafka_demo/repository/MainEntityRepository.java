package com.example.kafka_demo.repository;

import com.example.kafka_demo.data.MainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainEntityRepository extends JpaRepository<MainEntity, Long> {
}
