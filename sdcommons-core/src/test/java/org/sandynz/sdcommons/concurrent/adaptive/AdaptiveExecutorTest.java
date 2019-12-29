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
package org.sandynz.sdcommons.concurrent.adaptive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.sandynz.sdcommons.concurrent.ExecutorConstructionCfg;
import org.sandynz.sdcommons.concurrent.ThreadFactoryImpl;

/**
 * {@linkplain AdaptiveExecutor} test cases.
 *
 * @author sandynz
 */
@Slf4j
public class AdaptiveExecutorTest {

    @Test
    public void test1() {
        ExecutorConstructionCfg executorConstructionCfg = ExecutorConstructionCfg.builder()
                .setCorePoolSize(1).setMaxPoolSize(5)
                .setKeepAliveTime(60).setUnit(TimeUnit.SECONDS)
                .build();
        final BlockingQueue<Runnable> spareQueue = new LinkedBlockingQueue<>(100);
        List<Future<?>> futureList = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            AdaptiveDualBlockingQueue dualBlockingQueue = new AdaptiveDualBlockingQueue(new LinkedBlockingQueue<>(1), spareQueue);
            AdaptiveExecutor executor = new AdaptiveExecutor(executorConstructionCfg.toBuilder().setThreadFactory(new ThreadFactoryImpl("pool-" + i + "-")).build(), dualBlockingQueue);
            for (int j = 1; j < 20; j++) {
                int finalI = i;
                int finalJ = j;
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
        log.info("futureList.size={}", futureList.size());
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
