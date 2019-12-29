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

import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import org.sandynz.sdcommons.concurrent.adaptive.internal.AdaptiveFutureTask;
import org.sandynz.sdcommons.concurrent.adaptive.internal.AdaptiveRunnableFuture;

/**
 * TODO
 *
 * @author sandynz
 */
public abstract class AbstractAdaptiveExecutor extends AbstractExecutorService {

    private static final String DEFAULT_TASK_CATEGORY = "__DEFAULT__";
    private static final long DEFAULT_UPSTREAM_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(3);

    private <T> AdaptiveRunnableFuture<T> wrapTask(Runnable runnable, T value) {
        AdaptiveRunnable adaptiveRunnable;
        if (runnable instanceof AdaptiveRunnable) {
            adaptiveRunnable = (AdaptiveRunnable) runnable;
        } else {
            adaptiveRunnable = new AdaptiveRunnable() {
                @Override
                public AdaptiveTaskCfg getAdaptiveTaskCfg() {
                    return AdaptiveTaskCfg.builder().setTaskCategory(DEFAULT_TASK_CATEGORY).setUpstreamTimeoutMillis(DEFAULT_UPSTREAM_TIMEOUT_MILLIS).build();
                }

                @Override
                public void run() {
                    runnable.run();
                }
            };
        }
        return new AdaptiveFutureTask<>(adaptiveRunnable, value);
    }

    private <T> AdaptiveRunnableFuture<T> wrapTask(Callable<T> callable) {
        AdaptiveCallable<T> adaptiveCallable;
        if (callable instanceof AdaptiveCallable) {
            adaptiveCallable = (AdaptiveCallable<T>) callable;
        } else {
            adaptiveCallable = new AdaptiveCallable<T>() {
                @Override
                public T call() throws Exception {
                    return callable.call();
                }

                @Override
                public AdaptiveTaskCfg getAdaptiveTaskCfg() {
                    return AdaptiveTaskCfg.builder().setTaskCategory(DEFAULT_TASK_CATEGORY).setUpstreamTimeoutMillis(DEFAULT_UPSTREAM_TIMEOUT_MILLIS).build();
                }
            };
        }
        return new AdaptiveFutureTask<>(adaptiveCallable);
    }

    /**
     * {@inheritDoc}
     *
     * @param task must be instance of {@link AdaptiveCallable}
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) {
            throw new NullPointerException();
        }

        RunnableFuture<T> runnableFuture = wrapTask(task);
        execute(runnableFuture);
        return runnableFuture;
    }

    /**
     * {@inheritDoc}
     *
     * @param task must be instance of {@link AdaptiveRunnable}
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        verifyRunnable(task);
        RunnableFuture<T> runnableFuture = wrapTask(task, result);
        execute(runnableFuture);
        return runnableFuture;
    }

    /**
     * {@inheritDoc}
     *
     * @param task must be instance of {@link AdaptiveRunnable}
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    @Override
    public Future<?> submit(Runnable task) {
        verifyRunnable(task);
        RunnableFuture<Void> runnableFuture = wrapTask(task, null);
        execute(runnableFuture);
        return runnableFuture;
    }

    protected void verifyRunnable(Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }
    }

}
