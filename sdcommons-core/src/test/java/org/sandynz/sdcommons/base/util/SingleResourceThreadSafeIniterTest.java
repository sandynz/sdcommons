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
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.sandynz.sdcommons.concurrent.ExecutorServiceTestCfg;

/**
 * {@link SingleResourceThreadSafeIniter} test cases.
 *
 * @author sandynz
 */
@Slf4j
public class SingleResourceThreadSafeIniterTest {

    @Test
    public void testInitOnceAndGet() {
        ExecutorServiceTestCfg cfg = new ExecutorServiceTestCfg(1, 10, 50);
        SingleResourceThreadSafeIniter<ExecutorServiceTestCfg, ExecutorService> initer = new SingleResourceThreadSafeIniter<>();
        AtomicReference<Boolean> inited = new AtomicReference<>(false);
        List<ExecutorService> executorServiceList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            ExecutorService executorService = initer.initOnceAndGet(cfg, (input, oldResult) -> {
                if (!inited.compareAndSet(false, true)) {
                    throw new RuntimeException("inited not only once");
                }
                return new ThreadPoolExecutor(input.getCorePoolSize(), input.getMaxPoolSize(), input.getKeepAliveTime(), input.getUnit(), new LinkedBlockingQueue<>(input.getQueueCapacity()));
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

    private void testInitAndGet0(Predicate<String> cacheResourcePredicate, Predicate<String> resourceExpiredPredicate) {
        SingleResourceThreadSafeIniter<Void, String> initer = new SingleResourceThreadSafeIniter<>();
        List<String> stringList = new ArrayList<>();
        int loopCount = 3;
        AtomicInteger initCount = new AtomicInteger();
        boolean cacheResourcePredicateResult = false;
        boolean resourceExpiredPredicateResult = false;
        for (int i = 1; i <= loopCount; i++) {
            String result = initer.initAndGet(null, (input, oldResult) -> {
                initCount.incrementAndGet();
                return null;
            }, cacheResourcePredicate, resourceExpiredPredicate);
            cacheResourcePredicateResult = cacheResourcePredicate.test(result);
            resourceExpiredPredicateResult = resourceExpiredPredicate != null && resourceExpiredPredicate.test(result);
            stringList.add(result);
        }
        for (String result : stringList) {
            Assert.assertNull(result);
        }
        log.info("result null, cacheResourcePredicateResult={}, resourceExpiredPredicateResult={}, loopCount={}, initCount={}", cacheResourcePredicateResult, resourceExpiredPredicateResult, loopCount, initCount);
        Assert.assertEquals(cacheResourcePredicateResult && !resourceExpiredPredicateResult ? 1 : loopCount, initCount.get());
    }

    @Test
    public void testInitAndGet1() {
        testInitAndGet0(s -> true, null);
    }

    @Test
    public void testInitAndGet2() {
        testInitAndGet0(s -> false, null);
    }

    @Test
    public void testInitAndGet3() {
        testInitAndGet0(s -> true, Objects::isNull);
    }

}
