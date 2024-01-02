package com.example.kafka_demo.service;

import com.example.kafka_demo.config.configuration.properties.BrokersConfigProperties;
import com.example.kafka_demo.config.configuration.properties.RandomDataProperties;
import com.example.kafka_demo.data.*;
import com.example.kafka_demo.repository.MainEntityRepository;
import com.example.kafka_demo.repository.ThroughputDataRepository;
import com.example.kafka_demo.utils.RandomDataUtils;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Getter
public class DataTestUtilsService {

    private final MainEntityRepository outerEntityRepository;
    private final EntityManager entityManager;
    private final ThroughputDataRepository throughputDataRepository;
    private final RandomDataUtils randomDataUtils;
    private final ExecutorService executorService;
    private final BrokersConfigProperties brokersConfigProperties;
    private final RandomDataProperties randomDataProperties;
    private final BrokerConfigurationData brokerConfigurationData;

    @PostConstruct
    void truncateOnStartupInvoker() {
        if(brokersConfigProperties.truncateOnStartup()) {
            truncate();
        }
    }

    private void truncate() {
        throughputDataRepository.deleteAll();
    }

    public List<AccumulationData> loadData(long size) throws ExecutionException, InterruptedException {
       return executorService.execute(() -> randomDataUtils.generateRandomData(size));
    }

    public void saveProcessingData(ThroughputData.BrokerDomain brokerDomain, long processingTimeMillis, AccumulationData entity) {
        brokerDomain = brokersConfigProperties.isSslEnabled() ? ThroughputData.BrokerDomain.valueOf((brokerDomain + "_ssl").toUpperCase()) : brokerDomain;
        var throughputData = new ThroughputData(brokerDomain, processingTimeMillis);
        saveThroughPutData(throughputData);
        if(randomDataProperties.persistable()) {
            saveAccumulationData(entity);
        }
    }

    public void saveThroughPutData(ThroughputData throughputData) {
        throughputData.setBrokerConfigurationData(brokerConfigurationData);
        throughputDataRepository.save(throughputData);
    }

    public AccumulationData saveAccumulationData(AccumulationData outerEntity) {
        if (outerEntity.getId() != null) {
            Optional<AccumulationData> existingEntity = outerEntityRepository.findById(outerEntity.getId());

            if (existingEntity.isPresent() && !existingEntity.get().getSubEntities1().isEmpty()) {
                for (SubEntityTestData existingSubEntity : existingEntity.get().getSubEntities1()) {
                    if (outerEntity.getSubEntities1().stream().noneMatch(entity -> existingSubEntity.getId().equals(entity.getId()))) {
                        existingSubEntity.setAuditRemoveDate(LocalDateTime.now());
                        outerEntity.getSubEntities1().add(existingSubEntity);
                    }
                }
            }
            if (existingEntity.isPresent() && !existingEntity.get().getSubEntities1().isEmpty()) {
                for (SubEntityTestData2 existingSubEntity : existingEntity.get().getSubEntities2()) {
                    if (outerEntity.getSubEntities2().stream().noneMatch(entity -> existingSubEntity.getId().equals(entity.getId()))) {
                        existingSubEntity.setAuditRemoveDate(LocalDateTime.now());
                        outerEntity.getSubEntities2().add(existingSubEntity);
                    }
                }
            }
        }
        return outerEntityRepository.save(outerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void saveOuterEntityQuery(AccumulationData outerEntity) {
        AccumulationData existingEntity = outerEntityRepository.findById(outerEntity.getId()).orElse(null);

        updateItems(outerEntity);
    }

    private void updateItems(AccumulationData outerEntity) {
        Query query = entityManager.createNativeQuery("UPDATE SUB_ENTITY_TABLE SET eti_audit_rd = CURRENT_TIMESTAMP WHERE ETI_ETB_ID = :id AND ETI_ID NOT IN :items");
        Query query2 = entityManager.createNativeQuery("UPDATE SUB_ENTITY_TABLE2 SET eti2_audit_rd = CURRENT_TIMESTAMP WHERE ETI2_ETB_ID = :id AND ETI2_ID NOT IN :items");

        query2.setParameter("id", outerEntity.getId());
        query.setParameter("id", outerEntity.getId());
        List<Long> childIds = outerEntity.getSubEntities1().stream()
                .map(SubEntityTestData::getId)
                .toList();
        List<Long> childIds2 = outerEntity.getSubEntities2().stream()
                .map(SubEntityTestData2::getId)
                .toList();
        query.setParameter("items", childIds);
        query2.setParameter("items", childIds2);


        query.executeUpdate();

        outerEntityRepository.save(outerEntity);
    }
}
