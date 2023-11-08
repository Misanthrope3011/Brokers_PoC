package com.example.kafka_demo.service;

import com.example.kafka_demo.data.AccumulationData;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
@Service
public class ExecutorService implements Executor<List<AccumulationData>> {

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

    @Override
    public List<AccumulationData> execute(Callable<List<AccumulationData>> method) throws ExecutionException, InterruptedException {
        return executorService.submit(method).get();
    }

}
