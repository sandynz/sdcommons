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
package org.sandynz.sdcommons.concurrent.adaptive.internal;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.sandynz.sdcommons.base.statistic.LeapArray;
import org.sandynz.sdcommons.base.statistic.WindowWrap;

/**
 * Average stats {@link LeapArray} implementation for task call.
 *
 * @author sandynz
 */
public class TaskCallAvgStatsLeapArray extends LeapArray<TaskCallAvgStatsEntry, String> {

    /**
     * latest valid {@code StatsResult}, means {@code totalCount} greater than 0.
     */
    private volatile StatsResult latestValidStatsResult;

    /**
     * The total bucket count is: {@code sampleCount = intervalInMs / windowLengthInMs}.
     *
     * @param sampleCount  bucket count of the sliding window
     * @param intervalInMs the total time interval of this {@link LeapArray} in milliseconds
     */
    public TaskCallAvgStatsLeapArray(int sampleCount, int intervalInMs) {
        super(sampleCount, intervalInMs);
    }

    @Override
    public TaskCallAvgStatsEntry newEmptyBucket(long timeMillis) {
        return new TaskCallAvgStatsEntry();
    }

    @Override
    protected WindowWrap<TaskCallAvgStatsEntry> resetWindowTo(WindowWrap<TaskCallAvgStatsEntry> windowWrap, long startTime) {
        windowWrap.resetTo(startTime);
        windowWrap.value().reset();
        return windowWrap;
    }

    @Data
    @Accessors
    @ToString
    public static class StatsResult {

        private int totalCount;
        private double tps;
        private double avgRtMillis;
    }

    /**
     * Calculate current {@code StatsResult}.
     * If {@code totalCount} greater than 0, then return current result.
     * If {@code totalCount} is 0 and {@link #latestValidStatsResult} is not null, then return {@link #latestValidStatsResult}, else return null.
     *
     * @return {@link StatsResult} or null
     */
    public StatsResult calculateStatsResult() {
        long totalTimeMillis = 0;
        int totalCount = 0;
        long totalRt = 0;
        for (WindowWrap<TaskCallAvgStatsEntry> windowWrap : this.list()) {
            TaskCallAvgStatsEntry statsEntry = windowWrap.value();
            TaskCallAvgStatsEntry.Snapshot snapshot = statsEntry.getSnapshot();
            if (snapshot.getCount() == 0) {
                continue;
            }
            totalTimeMillis += windowWrap.windowLength();
            totalCount += snapshot.getCount();
            totalRt += snapshot.getRtSum();
        }
        if (totalCount == 0) {
            return this.latestValidStatsResult;
        }
        StatsResult result = new StatsResult();
        result.setTotalCount(totalCount);
        double tps = totalCount * 1.0D / totalTimeMillis * 1000;
        result.setTps(tps);
        double avgRtMillis = totalRt * 1.0D / totalCount;
        result.setAvgRtMillis(avgRtMillis);
        this.latestValidStatsResult = result;
        return result;
    }

}
