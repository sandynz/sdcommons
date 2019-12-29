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
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * {@linkplain ThreadPoolExecutor} extension.
 * <p>
 * {@link ExecutorAddWorkerStrategy} abstraction added.
 *
 * @author sandynz
 */
public class ExtendedThreadPoolExecutor extends ThreadPoolExecutor {

    public ExtendedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public ExtendedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public ExtendedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public ExtendedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    private ExecutorAddWorkerStrategy addWorkerStrategy;

    public ExecutorAddWorkerStrategy getAddWorkerStrategy() {
        return addWorkerStrategy;
    }

    public ExtendedThreadPoolExecutor setAddWorkerStrategy(ExecutorAddWorkerStrategy addWorkerStrategy) {
        if (this.addWorkerStrategy != null) {
            throw new IllegalStateException("addWorkerStrategy already set");
        }
        this.addWorkerStrategy = addWorkerStrategy;
        return this;
    }

    public ExtendedThreadPoolExecutor(ExecutorConstructionCfg cfg) {
        super(cfg.getCorePoolSize(), cfg.getMaxPoolSize(), cfg.getKeepAliveTime(), cfg.getUnit(), cfg.getWorkQueue(), cfg.getThreadFactory(), cfg.getHandler());
        if (cfg.getAddWorkerStrategy() != null) {
            this.setAddWorkerStrategy(cfg.getAddWorkerStrategy());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException("command is null");
        }
        ExecutorAddWorkerStrategy addWorkerStrategy = this.addWorkerStrategy;
        if (addWorkerStrategy == null) {
            addWorkerStrategy = new ExecutorAddWorkerOriginalStrategy();
        }
        addWorkerStrategy.addWorker(command, super.getExecutorExtContext());
    }

}
