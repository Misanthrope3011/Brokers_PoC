package com.example.kafka_demo.data;

import jakarta.persistence.*;
import lombok.*;
import org.apache.pulsar.shade.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "MAIN_ENTITY_TABLE")
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccumulationData implements Serializable {

    @Id
    @Column(name = "ETB_ID")
    private Long id;

    @Column(name = "ETB_DESC")
    private String desc;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "outer_entity_id")
    private List<NestedEntityInfo> subEntities1;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "outer_entity_id2")
    private List<NestedEntityInfo2> subEntities2;

    @Column(name = "ETB_IMAGE")
    private byte[] image;

    @Transient
    private long receiveTime;


}
