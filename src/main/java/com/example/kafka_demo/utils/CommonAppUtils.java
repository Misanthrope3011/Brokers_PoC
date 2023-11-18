package com.example.kafka_demo.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

@UtilityClass
@Slf4j
public class CommonAppUtils {

    public static void logException(Throwable e) {
        log.error(ExceptionUtils.getMessage(e));
    }

}
