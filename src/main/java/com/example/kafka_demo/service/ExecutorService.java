package com.example.kafka_demo.service;

import com.example.kafka_demo.data.MainEntity;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class ExecutorService implements Executor<List<MainEntity>> {

    @Getter
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

    @Override
    public List<MainEntity> execute(Callable<List<MainEntity>> method) throws ExecutionException, InterruptedException {
        return executorService.submit(method).get();
    }

}
