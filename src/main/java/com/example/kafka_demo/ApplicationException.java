package com.example.kafka_demo;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ApplicationException extends RuntimeException {

    public ApplicationException(Throwable cause) {
        super(ExceptionUtils.getMessage(cause));
    }

    public ApplicationException(String message) {
      super(message);
    }
    
}
