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
package org.sandynz.sdcommons.base.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * {@link SingleResourceThreadSafeIniter} test cases.
 *
 * @author sandynz
 */
@Slf4j
public class SingleResourceThreadSafeIniterTest {

    @Data
    @Accessors(chain = true)
    private static class ExecutorServiceCfg {

        private int corePoolSize;
        private int maxPoolSize;
        private int queryCapacity;

    }

    @Test
    public void testInitOnceAndGet() {
        ExecutorServiceCfg cfg = new ExecutorServiceCfg().setCorePoolSize(1).setMaxPoolSize(10).setQueryCapacity(50);
        SingleResourceThreadSafeIniter<ExecutorServiceCfg, ExecutorService> initer = new SingleResourceThreadSafeIniter<>();
        AtomicReference<Boolean> inited = new AtomicReference<>(false);
        List<ExecutorService> executorServiceList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            ExecutorService executorService = initer.initOnceAndGet(cfg, executorServiceCfg -> {
                if (!inited.compareAndSet(false, true)) {
                    throw new RuntimeException("inited not only once");
                }
                return new ThreadPoolExecutor(cfg.corePoolSize, cfg.maxPoolSize, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(cfg.getQueryCapacity()));
            });
            executorServiceList.add(executorService);
        }
        Assert.assertTrue(executorServiceList.size() > 0);
        Iterator<ExecutorService> iterator = executorServiceList.iterator();
        ExecutorService executorService = iterator.next();
        log.info("executorService={}", executorService);
        while (iterator.hasNext()) {
            Assert.assertSame(executorService, iterator.next());
        }
    }

    private void testInitAndGet0(boolean cacheNullResult) {
        SingleResourceThreadSafeIniter<Void, String> initer = new SingleResourceThreadSafeIniter<>();
        List<String> stringList = new ArrayList<>();
        int loopCount = 3;
        AtomicInteger initCount = new AtomicInteger();
        for (int i = 1; i <= loopCount; i++) {
            String result = initer.initAndGet(null, aVoid -> {
                initCount.incrementAndGet();
                return null;
            }, cacheNullResult);
            stringList.add(result);
        }
        for (String result : stringList) {
            Assert.assertNull(result);
        }
        log.info("result null, cacheNullResult={}, loopCount={}, initCount={}", cacheNullResult, loopCount, initCount);
        Assert.assertEquals(cacheNullResult ? 1 : loopCount, initCount.get());
    }

    @Test
    public void testInitAndGet1() {
        testInitAndGet0(true);
    }

    @Test
    public void testInitAndGet2() {
        testInitAndGet0(false);
    }

}
