package com.example.kafka_demo.utils;

import com.example.kafka_demo.data.AccumulationData;
import com.example.kafka_demo.data.NestedEntityInfo;
import com.example.kafka_demo.data.NestedEntityInfo2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Slf4j
@Service
public class RandomDataUtils {

    private final SecureRandom secureRandom;

    public List<AccumulationData> generateRandomData(long size) {
        var entityList = new ArrayList<AccumulationData>();
        var byteImage = new byte[2048];

        int subEntityArraySize = secureRandom.nextInt(3);

        for(int i = 0; i < size; i++) {
            var nestedEntityInfos = new ArrayList<NestedEntityInfo>();
            var nestedEntityInfos2 = new ArrayList<NestedEntityInfo2>();
            double progress = Double.parseDouble(String.format("%.3f", (double) i / (size)));
            synchronized(this) {
                if (Double.parseDouble(String.format("%.3f", progress * 100)) % 1 == 0) {
                    log.info("Waiting to initialize data: Progress : ".concat(String.format("%.0f", progress * 100)).concat("%"));
                }
            }
            for(int j = 0; j < subEntityArraySize; j++) {
                secureRandom.nextBytes(byteImage);

                var nestedEntityInfo1 = new NestedEntityInfo();
                nestedEntityInfo1 = NestedEntityInfo.builder()
                        .name(generateString(256))
                        .build();
                var nestedEntityInfo2 = new NestedEntityInfo2();
                nestedEntityInfo2 = NestedEntityInfo2.builder()
                        .description(generateString(256))
                        .build();
                nestedEntityInfos.add(nestedEntityInfo1);
                nestedEntityInfos2.add(nestedEntityInfo2);
            }

            AccumulationData entity;
            entity = AccumulationData.builder()
                    .id(secureRandom.nextLong(10000))
                    .image(byteImage)
                    .desc(generateString(100))
                    .subEntities1(nestedEntityInfos)
                    .subEntities2(nestedEntityInfos2)
                    .build();

            entityList.add(entity);
        }


        return entityList;
    }

    public String generateString(int targetStringLength) {
        int leftLimit = 97;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
