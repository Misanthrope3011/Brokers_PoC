package com.example.kafka_demo.service.utils;

import com.example.kafka_demo.data.AccumulationData;
import com.example.kafka_demo.data.NestedEntityInfo;
import com.example.kafka_demo.data.NestedEntityInfo2;
import com.example.kafka_demo.data.ThroughputData;

import java.util.Collections;

public class DateTestUtils {

    protected static int filterNullElementsFirstSubEntity(AccumulationData changedEntity) {
        return changedEntity.getSubEntities1()
                .stream()
                .filter(info -> info.getAuditRemoveDate() != null)
                .toList().size();
    }

    protected static int filterNullElementsSecondSubEntity(AccumulationData changedEntity) {
        return changedEntity.getSubEntities2()
                .stream()
                .filter(info -> info.getAuditRemoveDate() != null)
                .toList().size();
    }

    protected static AccumulationData prepareObjectWithNonExistingSubObjects() {
        NestedEntityInfo nestedEntityInfo = NestedEntityInfo.builder()
                .id(23L)
                .name("sample")
                .build();
        NestedEntityInfo2 nestedEntityInfo2 = NestedEntityInfo2.builder()
                .id(34L)
                .description("sample234")
                .build();

        return AccumulationData.builder()
                .brokerDomain(ThroughputData.BrokerDomain.KAFKA)
                .id(3445L)
                .desc("desc")
                .subEntities1(Collections.singletonList(nestedEntityInfo))
                .subEntities2(Collections.singletonList(nestedEntityInfo2))
                .build();
    }


    protected static AccumulationData prepareObjectWithExistingSubObjects() {
        NestedEntityInfo nestedEntityInfo = NestedEntityInfo.builder()
                .id(145L)
                .name("sample")
                .build();
        NestedEntityInfo2 nestedEntityInfo2 = NestedEntityInfo2.builder()
                .id(43L)
                .description("sample234")
                .build();

        return AccumulationData.builder()
                .brokerDomain(ThroughputData.BrokerDomain.KAFKA)
                .id(23L)
                .desc("desc")
                .subEntities1(Collections.singletonList(nestedEntityInfo))
                .subEntities2(Collections.singletonList(nestedEntityInfo2))
                .build();
    }


}
