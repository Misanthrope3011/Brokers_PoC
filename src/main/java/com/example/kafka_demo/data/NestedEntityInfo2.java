package com.example.kafka_demo.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "SUB_ENTITY_TABLE2")
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NestedEntityInfo2 implements Serializable {

    @Id
    @Column(name = "ETI2_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "ETI2_DESC", length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "ETI2_ETB_ID")
    private AccumulationData outerEntity;

    @Column(name = "ETI2_AUDIT_RD")
    private LocalDateTime auditRemoveDate;

}
