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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.sandynz.sdcommons.base.statistic.LeapArrayCfg;
import org.sandynz.sdcommons.base.statistic.LeapArrayListener;
import org.sandynz.sdcommons.base.util.SingleResourceInitializer;
import org.sandynz.sdcommons.concurrent.adaptive.internal.TaskCallAvgStatsLeapArray;
import org.sandynz.sdcommons.concurrent.adaptive.internal.TaskCallAvgStatsLeapArray.StatsResult;

/**
 * {@link AdaptiveExecutorStats} implementation which make decision on average stats result.
 * <p>
 * <ul>Goal:
 * <li>Serve tasks as more as possible for every task category, will not drop task except estimated RT is out of upstream timeout.
 * <li>Keep average actual measured RT for every task category.
 * <li>Maximum global throughput (balanced by previous goals, tasks arrival rate, and maximum thread pool size).
 * <li>Keep at least 1 thread of each pool for every task category (pool may be shared by several task categories).
 * </ul>
 * <p>
 * Task dropping:
 * If tasks increasing,
 * selector will pick task categories by task priority, then find related thread pools,
 * increase pool size and queue capacity until run out of resource, to achieve goal 1 and 2,
 * no matter rate limit.
 * When run out of resource, task dropping will occur.
 * <p>
 * Adaptive (adjusted on demand in real time):
 * 1) Every task category related thread pool maximum pool size.
 * 2) Every task category related thread pool queue capacity. TODO
 * 3) Total thread pool maximum pool size, bursts up to a percentage. TODO
 *
 * @author sandynz
 */
public class AdaptiveExecutorAvgStats implements AdaptiveExecutorStats {

    private final LeapArrayCfg leapArrayCfg;

    private final SingleResourceInitializer<LeapArrayCfg, TaskCallAvgStatsLeapArray> taskCategoryLeapArrayInitializer;

    private final AtomicReference<StatsResult> statsResultRef = new AtomicReference<>();

    public AdaptiveExecutorAvgStats() {
        this(new LeapArrayCfg(10, TimeUnit.SECONDS, 80));
    }

    public AdaptiveExecutorAvgStats(LeapArrayCfg leapArrayCfg) {
        if (leapArrayCfg == null) {
            throw new NullPointerException("leapArrayCfg null");
        }
        this.leapArrayCfg = leapArrayCfg;
        this.taskCategoryLeapArrayInitializer = new SingleResourceInitializer<>();
    }

    private class LeapArrayListenerImpl implements LeapArrayListener<String> {

        @Override
        public void bucketDeprecatedBeforeReset(String taskCategory) {
            TaskCallAvgStatsLeapArray leapArray = getLeapArray(taskCategory);
            TaskCallAvgStatsLeapArray.StatsResult statsResult = leapArray.calculateStatsResult();
            if (statsResult == null) {
                return;
            }
            statsResultRef.set(statsResult);
        }
    }

    @Override
    public Object beforeExecute(Thread thread, Runnable runnable) {
        return System.currentTimeMillis();
    }

    @Override
    public void afterExecute(Runnable runnable, Throwable throwable, Object beforeExecuteAttachment) {
        AdaptiveRunnable adaptiveRunnable = (AdaptiveRunnable) runnable;
        Long t1 = (Long) beforeExecuteAttachment;
        long timeMillis = System.currentTimeMillis();
        long rt = timeMillis - t1;
        String taskCategory = adaptiveRunnable.getAdaptiveTaskCfg().getTaskCategory();
        TaskCallAvgStatsLeapArray leapArray = getLeapArray(taskCategory);
        leapArray.currentWindow(timeMillis).value().addAll(rt);
    }

    private TaskCallAvgStatsLeapArray getLeapArray(String taskCategory) {
        return this.taskCategoryLeapArrayInitializer.initOnceAndGet(
                this.leapArrayCfg,
                (cfg, taskCallAvgStatsLeapArray) -> {
                    TaskCallAvgStatsLeapArray result = new TaskCallAvgStatsLeapArray(cfg.getSampleCount(), cfg.getIntervalInMs());
                    result.setIdentifier(taskCategory);
                    result.addListener(new LeapArrayListenerImpl());
                    return result;
                }
        );
    }

    @Override
    public int calculateSuitableQueueSize(AdaptiveRunnable adaptiveRunnable) {
        return 0;//TODO
    }

    @Override
    public boolean couldCommit(AdaptiveRunnable adaptiveRunnable) {
        return false;//TODO
    }

}
