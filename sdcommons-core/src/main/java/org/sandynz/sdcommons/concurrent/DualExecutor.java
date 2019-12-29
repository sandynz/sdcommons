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
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * Dual executor, includes a {@code baseExecutor} and {@code spareQueue}.
 * <p>
 * In general, {@code baseExecutor} is owned by only one {@linkplain DualExecutor}, used for certain tasks.
 * {@code spareExecutor} should be shared by several {@linkplain DualExecutor}.
 *
 * @author sandynz
 */
@Slf4j
public class DualExecutor extends AbstractExecutorService {

    private final ThreadPoolExecutor baseExecutor;
    private final ThreadPoolExecutor spareExecutor;

    /**
     * @param baseExecutor  base executor, {@link LightweightAbortPolicy} could be used
     * @param spareExecutor spare executor, {@linkplain Runnable} will be submitted to here when it is rejected by {@code baseExecutor}
     */
    public DualExecutor(ThreadPoolExecutor baseExecutor, ThreadPoolExecutor spareExecutor) {
        if (baseExecutor == null || spareExecutor == null) {
            throw new NullPointerException();
        }

        this.baseExecutor = baseExecutor;
        this.spareExecutor = spareExecutor;
    }

    @Override
    public void shutdown() {
        baseExecutor.shutdown();
        spareExecutor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        List<Runnable> list1 = baseExecutor.shutdownNow();
        List<Runnable> list2 = spareExecutor.shutdownNow();
        List<Runnable> result = new ArrayList<>(list1.size() + list2.size());
        result.addAll(list1);
        result.addAll(list2);
        return result;
    }

    @Override
    public boolean isShutdown() {
        return baseExecutor.isShutdown() && spareExecutor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return baseExecutor.isTerminated() && spareExecutor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long t = unit.toNanos(timeout);
        long t1 = t / 2;
        boolean result = baseExecutor.awaitTermination(t1, TimeUnit.NANOSECONDS);
        result &= spareExecutor.awaitTermination((t - t1), TimeUnit.NANOSECONDS);
        return result;
    }

    @Override
    public void execute(Runnable command) {
        try {
            baseExecutor.execute(command);
        } catch (RejectedExecutionException e) {
            if (log.isDebugEnabled()) {
                log.debug("command rejected by baseExecutor, command={}", command, e);
            }
            spareExecutor.execute(command);
        }
    }

}
