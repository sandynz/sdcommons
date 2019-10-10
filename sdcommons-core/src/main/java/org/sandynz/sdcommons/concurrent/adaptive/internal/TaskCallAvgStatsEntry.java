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

import java.util.concurrent.atomic.LongAdder;
import lombok.Data;
import lombok.ToString;

/**
 * Average stats entry for task call.
 *
 * @author sandynz
 */
public class TaskCallAvgStatsEntry {

    private enum StatType {

        COUNT, RT
    }

    @Data
    @ToString
    static class Snapshot {

        private final int count;
        private final long rtSum;

        Snapshot(int count, long rtSum) {
            this.count = count;
            this.rtSum = rtSum;
        }
    }

    private final LongAdder[] longAdders;

    TaskCallAvgStatsEntry() {
        this.longAdders = new LongAdder[StatType.values().length];
    }

    void reset() {
        for (LongAdder adder : longAdders) {
            adder.reset();
        }
    }

    public void addAll(long rt) {
        this.longAdders[StatType.COUNT.ordinal()].increment();
        this.longAdders[StatType.RT.ordinal()].add(rt);
    }

    Snapshot getSnapshot() {
        long rtSum = this.longAdders[StatType.RT.ordinal()].sum();
        int count = this.longAdders[StatType.COUNT.ordinal()].intValue();
        return new Snapshot(count, rtSum);
    }

}
