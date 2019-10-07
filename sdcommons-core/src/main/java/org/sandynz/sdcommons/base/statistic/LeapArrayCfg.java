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
package org.sandynz.sdcommons.base.statistic;

import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * {@link LeapArray} construction configuration.
 *
 * @author sandynz
 */
@Data
@Getter
@ToString
public class LeapArrayCfg {

    /**
     * bucket count of the sliding window
     */
    private final int sampleCount;
    /**
     * time interval unit of {@link LeapArray}
     */
    private final TimeUnit intervalUnit;
    /**
     * time interval of {@link LeapArray}
     */
    private final int interval;

    /**
     * @throws NullPointerException     if intervalUnit is null
     * @throws IllegalArgumentException 1) if sampleCount / interval less than or equals to zero, 2) or interval overflow
     */
    public LeapArrayCfg(int sampleCount, TimeUnit intervalUnit, int interval) {
        if (intervalUnit == null) {
            throw new NullPointerException("intervalUnit null");
        }
        if (sampleCount <= 0 || interval <= 0) {
            throw new IllegalArgumentException("sampleCount or interval le 0");
        }
        long millis = intervalUnit.toMillis(interval);
        if (millis > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("interval overflow");
        }

        this.sampleCount = sampleCount;
        this.intervalUnit = intervalUnit;
        this.interval = interval;
    }

    public int getIntervalInMs() {
        return (int) intervalUnit.toMillis(interval);
    }

}
