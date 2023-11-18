package com.example.kafka_demo.utils;

import com.example.kafka_demo.config.configuration.properties.RandomDataProperties;
import com.example.kafka_demo.data.AccumulationData;
import com.example.kafka_demo.data.NestedEntityInfo;
import com.example.kafka_demo.data.NestedEntityInfo2;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

@RequiredArgsConstructor
@Slf4j
@Service
public class RandomDataUtils {

    private final SecureRandom secureRandom;
    private final RandomDataProperties randomDataProperties;
    private final Object monitor = new ObjectMapper();

    public List<AccumulationData> generateRandomData(long size) {
        var byteImage = new byte[randomDataProperties.imageSizeBytes()];
        var atomicInteger = new AtomicInteger();
        int subEntityArraySize = secureRandom.nextInt(3);

        return LongStream.range(0, size).boxed().parallel()
                .map(value -> createAccumulationData(size, atomicInteger, subEntityArraySize, byteImage))
                .toList();
    }

    private AccumulationData createAccumulationData(long size, AtomicInteger atomicInteger, int subEntityArraySize, byte[] byteImage) {
        var nestedEntityInfos = new ArrayList<NestedEntityInfo>();
        var nestedEntityInfos2 = new ArrayList<NestedEntityInfo2>();
        synchronized (monitor) {
            atomicInteger.incrementAndGet();
            double progress = Double.parseDouble(String.format("%.8f", (double) atomicInteger.get() / size));
            if (Double.parseDouble(String.format("%.8f", progress * 100)) % 1 == 0) {
                log.info("Waiting to initialize data: Progress : ".concat(String.format("%.0f", progress * 100)).concat("%"));
            }
        }

        for (int j = 0; j < subEntityArraySize; j++) {
            secureRandom.nextBytes(byteImage);

            var nestedEntityInfo1 = new NestedEntityInfo();
            nestedEntityInfo1 = NestedEntityInfo.builder()
                    .name(generateString(randomDataProperties.nameSizeBytes()))
                    .build();
            var nestedEntityInfo2 = new NestedEntityInfo2();
            nestedEntityInfo2 = NestedEntityInfo2.builder()
                    .description(generateString(randomDataProperties.descSizeBytes()))
                    .build();
            nestedEntityInfos.add(nestedEntityInfo1);
            nestedEntityInfos2.add(nestedEntityInfo2);
        }

        AccumulationData entity;
        entity = AccumulationData.builder()
                .id(secureRandom.nextLong(10000))
                .image(byteImage)
                .desc(generateString(randomDataProperties.descSizeBytes()))
                .subEntities1(nestedEntityInfos)
                .subEntities2(nestedEntityInfos2)
                .build();

        return entity;
    }

    public String generateString(int targetStringLength) {
        int leftLimit = 97;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(targetStringLength, leftLimit, rightLimit + 1)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
