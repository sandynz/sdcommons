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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.junit.Assert;
import org.junit.Test;

/**
 * {@link ExtendedThreadPoolExecutor} test cases.
 *
 * @author sandynz
 */
@Slf4j
public class ExtendedThreadPoolExecutorTest {

    private void test0(ExecutorService executorService, int runnableCount, Range<Integer> expectedThreadCount) {
        List<Future<Integer>> futureList = new ArrayList<>(runnableCount);
        Set<String> threadNameSet = new HashSet<>();
        for (int index = 1; index <= runnableCount; index++) {
            int finalIndex = index;
            Future<Integer> future = executorService.submit(() -> {
                log.info("run, index={}", finalIndex);
                threadNameSet.add(Thread.currentThread().getName());
                TimeUnit.MILLISECONDS.sleep(100);
                return finalIndex;
            });
            futureList.add(future);
        }
        for (Future<Integer> future : futureList) {
            Integer result = null;
            try {
                result = future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.info("get result failed", e);
            }
            log.info("result={}", result);
        }
        List<String> threadNameList = new ArrayList<>(threadNameSet);
        Collections.sort(threadNameList);
        log.info("threadNameSet={}", threadNameList);
        log.info("threadNameSet.size={}, expectedThreadCount={}", threadNameSet.size(), expectedThreadCount);
        Assert.assertTrue(expectedThreadCount.contains(threadNameSet.size()));
    }

    private ExecutorService createExecutorService(ExecutorServiceTestCfg cfg) {
        return new ExtendedThreadPoolExecutor(
                cfg.getCorePoolSize(), cfg.getMaxPoolSize(),
                cfg.getKeepAliveTime(), cfg.getUnit(),
                new LinkedBlockingQueue<>(cfg.getQueueCapacity()),
                new ThreadFactoryImpl(cfg.getThreadNamePrefix()))
                .setAddWorkerStrategy(cfg.getAddWorkerStrategy());
    }

    @Test
    public void testOriginalStrategy() {
        ExecutorServiceTestCfg cfg = new ExecutorServiceTestCfg(1, 10, 500)
                .setAddWorkerStrategy(new ExecutorAddWorkerOriginalStrategy());
        ExecutorService executorService = createExecutorService(cfg);
        test0(executorService, 10, Range.between(1, 1));
    }

    @Test
    public void testEagerStrategy() {
        ExecutorServiceTestCfg cfg = new ExecutorServiceTestCfg(1, 10, 500)
                .setAddWorkerStrategy(new ExecutorAddWorkerEagerStrategy());
        ExecutorService executorService = createExecutorService(cfg);
        test0(executorService, 20, Range.between(10, 10));
    }

}
