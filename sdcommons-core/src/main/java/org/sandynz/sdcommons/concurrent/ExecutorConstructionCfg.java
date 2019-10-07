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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Configuration used to create {@link java.util.concurrent.Executor}.
 *
 * @author sandynz
 */
@Data
@Accessors(chain = true)
@ToString
public class ExecutorConstructionCfg {

    private final int corePoolSize;
    private final int maxPoolSize;
    private long keepAliveTime = 60L;
    private TimeUnit unit = TimeUnit.SECONDS;

    private final QueueCfg queueCfg;

    private String threadNamePrefix = "pool-";

    private ExecutorAddWorkerStrategy addWorkerStrategy;

    @Data
    @Accessors(chain = true)
    @ToString
    public static class QueueCfg {

        private boolean bounded = true;
        private int capacity = 1000;
        private boolean preferHigherThroughput = true;

        public void setCapacity(int capacity) {
            if (capacity <= 0) {
                throw new IllegalArgumentException("capacity le 0");
            }
            this.capacity = capacity;
        }
    }

    private Function<QueueCfg, BlockingQueue<Runnable>> createQueueFunction = cfg -> {
        if (cfg.bounded) {
            if (cfg.capacity <= 100 && !cfg.preferHigherThroughput) {
                return new ArrayBlockingQueue<>(cfg.capacity);
            } else {
                return new LinkedBlockingQueue<>(cfg.capacity);
            }
        } else {
            return new LinkedBlockingQueue<>();
        }
    };

    public ExecutorConstructionCfg(int corePoolSize, int maxPoolSize, QueueCfg queueCfg) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.queueCfg = queueCfg;
    }

}
