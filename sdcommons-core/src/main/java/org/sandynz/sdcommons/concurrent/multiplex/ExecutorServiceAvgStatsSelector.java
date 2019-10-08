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
package org.sandynz.sdcommons.concurrent.multiplex;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.experimental.Accessors;
import org.sandynz.sdcommons.base.statistic.LeapArrayCfg;
import org.sandynz.sdcommons.base.statistic.LeapArrayListener;
import org.sandynz.sdcommons.base.util.MultipleResourcesInitializer;
import org.sandynz.sdcommons.concurrent.ExecutorConstructionCfg;
import org.sandynz.sdcommons.concurrent.multiplex.internal.TaskCallAvgStatsLeapArray;

/**
 * {@link ExecutorServiceSelector} implementation which make decision on average stats result.
 *
 * @author sandynz
 */
public class ExecutorServiceAvgStatsSelector implements ExecutorServiceSelector {

    @Data
    @Accessors(chain = true)
    public static class Builder {

        private ExecutorConstructionCfg executorConstructionCfg;
        private LeapArrayCfg leapArrayCfg;

        public ExecutorServiceAvgStatsSelector build() {
            return new ExecutorServiceAvgStatsSelector(this);
        }
    }

    private final ExecutorConstructionCfg executorConstructionCfg;

    private final LeapArrayCfg leapArrayCfg;

    private final String defaultTaskCategory = "__DEFAULT__";

    private final MultipleResourcesInitializer<String/*taskCategory*/, LeapArrayCfg, TaskCallAvgStatsLeapArray> taskCategoryLeapArrayInitializer;

    private final ConcurrentMap<String/*taskCategory*/, TaskCallAvgStatsLeapArray.StatsResult> taskCategoryStatsResultMap;

    private ExecutorServiceAvgStatsSelector(Builder builder) {
        if (builder.executorConstructionCfg == null) {
            throw new NullPointerException("executorConstructionCfg null");
        }
        this.executorConstructionCfg = builder.executorConstructionCfg;
        if (builder.leapArrayCfg != null) {
            this.leapArrayCfg = builder.leapArrayCfg;
        } else {
            this.leapArrayCfg = new LeapArrayCfg(10, TimeUnit.SECONDS, 80);
        }

        this.taskCategoryLeapArrayInitializer = new MultipleResourcesInitializer<>();
        this.taskCategoryStatsResultMap = new ConcurrentHashMap<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Object beforeExecute(Thread thread, MultiplexRunnable runnable) {
        return System.currentTimeMillis();
    }

    @Override
    public void afterExecute(MultiplexRunnable runnable, Throwable throwable, Object beforeExecuteAttachment) {
        Long t1 = (Long) beforeExecuteAttachment;
        long timeMillis = System.currentTimeMillis();
        long rt = timeMillis - t1;
        String taskCategory = runnable.getCategorizableTaskCfg().getTaskCategory();
        TaskCallAvgStatsLeapArray leapArray = getLeapArray(taskCategory);
        leapArray.currentWindow(timeMillis).value().addAll(rt);
    }

    private TaskCallAvgStatsLeapArray getLeapArray(String taskCategory) {
        return this.taskCategoryLeapArrayInitializer.initOnceAndGet(
                taskCategory, this.leapArrayCfg,
                (cfg, taskCallAvgStatsLeapArray) -> {
                    TaskCallAvgStatsLeapArray result = new TaskCallAvgStatsLeapArray(cfg.getSampleCount(), cfg.getIntervalInMs());
                    result.setIdentifier(taskCategory);
                    result.addListener(new LeapArrayListenerImpl());
                    return result;
                }
        );
    }

    private class LeapArrayListenerImpl implements LeapArrayListener<String> {

        @Override
        public void bucketDeprecatedAndBeforeReset(String taskCategory) {
            TaskCallAvgStatsLeapArray leapArray = getLeapArray(taskCategory);
            TaskCallAvgStatsLeapArray.StatsResult statsResult = leapArray.calculateStatsResult();
            taskCategoryStatsResultMap.put(taskCategory, statsResult);
        }
    }

    @Override
    public ExecutorService select(MultiplexRunnable runnable) {
        CategorizableTaskCfg categorizableTaskCfg = runnable.getCategorizableTaskCfg();
        String taskCategory = categorizableTaskCfg.getTaskCategory();
        int taskPriority = categorizableTaskCfg.getTaskPriority();
        TaskCallAvgStatsLeapArray.StatsResult statsResult = this.taskCategoryStatsResultMap.get(taskCategory);
        return null;//TODO
    }

    @Override
    public Iterator<ExecutorService> iterator() {
        return null;//TODO
    }
}
