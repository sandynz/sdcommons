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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.sandynz.sdcommons.validation.Validations;

/**
 * Configuration used to create {@link java.util.concurrent.Executor}.
 *
 * @author sandynz
 */
@Data
@Accessors(chain = true)
@ToString
public class ExecutorConstructionCfg {

    @Data
    @Accessors(chain = true)
    @ToString
    public static class Builder {

        @Min(0)
        private int corePoolSize;
        @Min(1)
        private int maxPoolSize;
        @Min(1)
        private long keepAliveTime = 60L;
        @NotNull
        private TimeUnit unit = TimeUnit.SECONDS;
        @NotNull
        private QueueCfg queueCfg;
        @NotBlank
        private String threadNamePrefix = "pool-";
        private ExecutorAddWorkerStrategy addWorkerStrategy;
        private Function<QueueCfg, BlockingQueue<Runnable>> createQueueFunction;

        public ExecutorConstructionCfg build() {
            return new ExecutorConstructionCfg(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final int corePoolSize;
    //TODO adaptiveMaxPoolSizeEnabled; adaptiveMaxPoolSizeBurstPercentage;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final TimeUnit unit;

    private final QueueCfg queueCfg;

    private final String threadNamePrefix;

    private final ExecutorAddWorkerStrategy addWorkerStrategy;

    private final Function<QueueCfg, BlockingQueue<Runnable>> createQueueFunction;

    private ExecutorConstructionCfg(Builder builder) {
        boolean validateRet = Validations.validateBean(builder);
        if (!validateRet) {
            throw new IllegalArgumentException("invalid settings");
        }

        this.corePoolSize = builder.corePoolSize;
        this.maxPoolSize = builder.maxPoolSize;
        this.keepAliveTime = builder.keepAliveTime;
        this.unit = builder.unit;
        this.queueCfg = builder.queueCfg;
        this.threadNamePrefix = builder.threadNamePrefix;
        if (builder.addWorkerStrategy != null) {
            this.addWorkerStrategy = builder.addWorkerStrategy;
        } else {
            //TODO
            this.addWorkerStrategy = new ExecutorAddWorkerEagerStrategy();
        }
        if (builder.createQueueFunction != null) {
            this.createQueueFunction = builder.createQueueFunction;
        } else {
            this.createQueueFunction = cfg -> {
                if (cfg.bounded) {
                    if (cfg.initialCapacity <= 100 && !cfg.preferHigherThroughput) {
                        return new ArrayBlockingQueue<>(cfg.initialCapacity);
                    } else {
                        return new LinkedBlockingQueue<>(cfg.initialCapacity);
                    }
                } else {
                    return new LinkedBlockingQueue<>();
                }
            };
        }
    }

    @Data
    @Accessors(chain = true)
    @ToString
    public static class QueueCfg {

        @Data
        @Accessors(chain = true)
        @ToString
        public static class Builder {

            private boolean bounded = true;
            @Min(1)
            private int initialCapacity = 1000;
            private boolean preferHigherThroughput = true;

            public QueueCfg build() {
                return new QueueCfg(this);
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        private final boolean bounded;
        //TODO adaptiveCapacityEnabled; adaptiveCapacityRange;
        private final int initialCapacity;
        private final boolean preferHigherThroughput;

        private QueueCfg(Builder builder) {
            boolean validateRet = Validations.validateBean(builder);
            if (!validateRet) {
                throw new IllegalArgumentException("invalid settings");
            }

            this.bounded = builder.bounded;
            this.initialCapacity = builder.initialCapacity;
            this.preferHigherThroughput = builder.preferHigherThroughput;
        }
    }

}
