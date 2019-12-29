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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.Min;
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
        //@NotNull nullable
        private BlockingQueue<Runnable> workQueue;

        private ThreadFactory threadFactory;
        private RejectedExecutionHandler handler;

        private ExecutorAddWorkerStrategy addWorkerStrategy;

        public ExecutorConstructionCfg build() {
            return new ExecutorConstructionCfg(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final TimeUnit unit;
    private final BlockingQueue<Runnable> workQueue;

    private final ThreadFactory threadFactory;
    private final RejectedExecutionHandler handler;

    private final ExecutorAddWorkerStrategy addWorkerStrategy;

    private ExecutorConstructionCfg(Builder builder) {
        boolean validateRet = Validations.validateBean(builder);
        if (!validateRet) {
            throw new IllegalArgumentException("invalid settings");
        }

        this.corePoolSize = builder.corePoolSize;
        this.maxPoolSize = builder.maxPoolSize;
        this.keepAliveTime = builder.keepAliveTime;
        this.unit = builder.unit;
        this.workQueue = builder.workQueue;

        if (builder.threadFactory != null) {
            this.threadFactory = builder.threadFactory;
        } else {
            this.threadFactory = Executors.defaultThreadFactory();
        }
        if (builder.handler != null) {
            this.handler = builder.handler;
        } else {
            this.handler = new ExtendedThreadPoolExecutor.AbortPolicy();
        }
        this.addWorkerStrategy = builder.addWorkerStrategy;
    }

    public Builder toBuilder() {
        return new Builder()
                .setCorePoolSize(corePoolSize).setMaxPoolSize(maxPoolSize)
                .setKeepAliveTime(keepAliveTime).setUnit(unit)
                .setWorkQueue(workQueue)
                .setThreadFactory(threadFactory).setHandler(handler)
                .setAddWorkerStrategy(addWorkerStrategy);
    }

}
