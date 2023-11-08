package com.example.kafka_demo.data;

import jakarta.persistence.*;
import lombok.*;
import org.apache.pulsar.shade.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@Table(name = "SUB_ENTITY_TABLE")
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NestedEntityInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ETI_ID")
    private Long id;

    @Column(name = "ETI_NAME", length = 1000)
    private String name;

    @ManyToOne
    @JoinColumn(name = "ETI_ETB_ID")
    private AccumulationData outerEntity;

    @Column(name = "ETI_AUDIT_RD")
    private LocalDateTime auditRemove;

}
