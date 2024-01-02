package com.example.kafka_demo.service;

import com.example.kafka_demo.data.AccumulationData;
import com.example.kafka_demo.repository.MainEntityRepository;
import com.example.kafka_demo.service.utils.DateTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Slf4j
class DataTestUtilsServiceTest extends DateTestUtils {

    @Mock
    private MainEntityRepository mainEntityRepository;

    @InjectMocks
    private DataTestUtilsService dataTestUtilsService;

    @Captor
    private ArgumentCaptor<AccumulationData> changedEntity;

    @Test
    void Should_RemoveOneElement_WhenSavingUpdatedEntity() {
        var preparedObject = prepareObjectWithNonExistingSubObjects();
        Mockito.when(mainEntityRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(preparedObject));
        var object = readAndUpdateEntityFromFile();

        dataTestUtilsService.saveAccumulationData(object);

        Mockito.verify(mainEntityRepository).save(changedEntity.capture());
        Assertions.assertEquals(1, filterNullElementsFirstSubEntity(changedEntity.getValue()));
        Assertions.assertEquals(1, filterNullElementsSecondSubEntity(changedEntity.getValue()));
    }

    @Test
    void Should_RemoveNotRemoveAnyElement_WhenSavingUpdatedEntity() {
        var preparedObject = prepareObjectWithExistingSubObjects();
        Mockito.when(mainEntityRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(preparedObject));
        var object = readAndUpdateEntityFromFile();

        dataTestUtilsService.saveAccumulationData(object);

        Mockito.verify(mainEntityRepository).save(changedEntity.capture());
        Assertions.assertEquals(0, filterNullElementsFirstSubEntity(changedEntity.getValue()));
        Assertions.assertEquals(0, filterNullElementsSecondSubEntity(changedEntity.getValue()));

    }

    private AccumulationData readAndUpdateEntityFromFile() {
        File file = new File("src/test/java/resources/payload.json");
        try (FileInputStream inputStream = new FileInputStream(file)) {
            var objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStream.readAllBytes(), AccumulationData.class);
        } catch (IOException ex) {
           log.error("Unexpected error during test: " + ExceptionUtils.getMessage(ex));
        }
        return null;
    }

}