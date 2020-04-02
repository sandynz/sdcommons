/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sandynz.sdcommons.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * {@linkplain DualExecutor} test cases.
 *
 * @author sandynz
 */
@Slf4j
public class DualExecutorTest {

    @Test
    public void test1() {
        ExecutorConstructionCfg executorConstructionCfg = ExecutorConstructionCfg.builder()
                .setCorePoolSize(1).setMaxPoolSize(5)
                .setKeepAliveTime(60).setUnit(TimeUnit.SECONDS)
                .build();
        final BlockingQueue<Runnable> spareQueue = new LinkedBlockingQueue<>(100);
        final ThreadPoolExecutor spareExecutor = new ExtendedThreadPoolExecutor(executorConstructionCfg.toBuilder()
                .setThreadFactory(new ThreadFactoryImpl("spare-"))
                .setWorkQueue(spareQueue)
                .setMaxPoolSize(10)
                .setAddWorkerStrategy(new ExecutorAddWorkerEagerStrategy())
                .build());
        int taskCount = 0;
        List<Future<?>> futureList = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            ThreadPoolExecutor baseExecutor = new ExtendedThreadPoolExecutor(executorConstructionCfg.toBuilder()
                    .setThreadFactory(new ThreadFactoryImpl("pool-" + i + "-"))
                    .setWorkQueue(new LinkedBlockingQueue<>(10))
                    .setAddWorkerStrategy(new ExecutorAddWorkerEagerStrategy())
                    .setHandler(new LightweightAbortPolicy())
                    .build());
            DualExecutor executor = new DualExecutor(baseExecutor, spareExecutor);
            for (int j = 1; j <= 20; j++) {
                int finalI = i;
                int finalJ = j;
                taskCount++;
                Future<?> future = executor.submit(() -> {
                    log.info("i={}, j={}, spareQueue.size={}", finalI, finalJ, spareQueue.size());
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        log.error("ex caught", e);
                    }
                });
                futureList.add(future);
            }
        }
        log.info("taskCount={}, futureList.size={}", taskCount, futureList.size());
        for (Future<?> future : futureList) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.info("future.get ex caught", e);
            }
        }
        log.info("end");
    }

}
