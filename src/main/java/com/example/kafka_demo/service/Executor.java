package com.example.kafka_demo.service;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface Executor<T> {

    T execute(Callable<T> method) throws Exception;

}
