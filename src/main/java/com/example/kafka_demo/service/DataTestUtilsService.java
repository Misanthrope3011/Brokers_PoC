package com.example.kafka_demo.service;

import com.example.kafka_demo.data.MainEntity;
import com.example.kafka_demo.data.NestedEntityInfo;
import com.example.kafka_demo.data.NestedEntityInfo2;
import com.example.kafka_demo.data.ThrougputData;
import com.example.kafka_demo.repository.MainEntityRepository;
import com.example.kafka_demo.repository.ThroughtputDataRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DataTestUtilsService {

    private final MainEntityRepository outerEntityRepository;
    private final EntityManager entityManager;
    private final ThroughtputDataRepository throughtputDataRepository;

    public void truncate() {
        throughtputDataRepository.deleteAll();
    }
    public void saveThroughtPutData(ThrougputData througputData) {
        throughtputDataRepository.save(througputData);
    }

    public MainEntity saveOuterEntity(MainEntity outerEntity) {
        if (outerEntity.getId() != null) {
            Optional<MainEntity> existingEntity = outerEntityRepository.findById(outerEntity.getId());

            if (existingEntity.isPresent() && !existingEntity.get().getSubEntities1().isEmpty()) {
                for (NestedEntityInfo existingSubEntity : existingEntity.get().getSubEntities1()) {
                    if (outerEntity.getSubEntities1().stream().noneMatch(entity -> existingSubEntity.getId().equals(entity.getId()))) {
                        existingSubEntity.setAuditRemove(LocalDateTime.now());
                        outerEntity.getSubEntities1().add(existingSubEntity);
                    }
                }
            }
            if (existingEntity.isPresent() && !existingEntity.get().getSubEntities1().isEmpty()) {
                for (NestedEntityInfo2 existingSubEntity : existingEntity.get().getSubEntities2()) {
                    if (outerEntity.getSubEntities2().stream().noneMatch(entity -> existingSubEntity.getId().equals(entity.getId()))) {
                        existingSubEntity.setAuditRemoveDate(LocalDateTime.now());
                        outerEntity.getSubEntities2().add(existingSubEntity);
                    }
                }
            }
        }
        return outerEntityRepository.save(outerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOuterEntityQuery(MainEntity outerEntity) {
        MainEntity existingEntity = outerEntityRepository.findById(outerEntity.getId()).orElse(null);

        updateItems(outerEntity);
    }

    private void updateItems(MainEntity outerEntity) {
        Query query = entityManager.createNativeQuery("UPDATE SUB_ENTITY_TABLE SET eti_audit_rd = CURRENT_TIMESTAMP WHERE ETI_ETB_ID = :id AND ETI_ID NOT IN :items");
        Query query2 = entityManager.createNativeQuery("UPDATE SUB_ENTITY_TABLE2 SET eti2_audit_rd = CURRENT_TIMESTAMP WHERE ETI2_ETB_ID = :id AND ETI2_ID NOT IN :items");

        query2.setParameter("id", outerEntity.getId());
        query.setParameter("id", outerEntity.getId());
        List<Long> childIds = outerEntity.getSubEntities1().stream()
                .map(NestedEntityInfo::getId)
                .toList();
        List<Long> childIds2 = outerEntity.getSubEntities2().stream()
                .map(NestedEntityInfo2::getId)
                .toList();
        query.setParameter("items", childIds);
        query2.setParameter("items", childIds2);


        query.executeUpdate();

        // Save the updated OuterEntity
        outerEntityRepository.save(outerEntity);
    }
}