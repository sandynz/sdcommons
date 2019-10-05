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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

public class MultiplexExecutorService extends AbstractExecutorService {

    private final ExecutorServiceSelector executorServiceSelector;

    public MultiplexExecutorService(ExecutorServiceSelector executorServiceSelector) {
        this.executorServiceSelector = executorServiceSelector;
    }

    @Override
    public void shutdown() {
        for (ExecutorService executorService : executorServiceSelector) {
            executorService.shutdown();
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        List<Runnable> result = new ArrayList<>();
        for (ExecutorService executorService : executorServiceSelector) {
            result.addAll(executorService.shutdownNow());
        }
        return result;
    }

    @Override
    public boolean isShutdown() {
        for (ExecutorService executorService : executorServiceSelector) {
            if (!executorService.isShutdown()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isTerminated() {
        for (ExecutorService executorService : executorServiceSelector) {
            if (!executorService.isTerminated()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        unit.sleep(timeout);
        return true;
    }

    private <T> MultiplexRunnableFuture<T> wrapTask(MultiplexRunnable runnable, T value) {
        return new MultiplexFutureTask<T>(runnable, value);
    }

    private <T> MultiplexRunnableFuture<T> wrapTask(MultiplexCallable callable) {
        return new MultiplexFutureTask<T>(callable);
    }

    /**
     * {@inheritDoc}
     *
     * @param task must be instance of {@link MultiplexCallable}
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) {
            throw new NullPointerException();
        }
        if (!(task instanceof MultiplexCallable)) {
            throw new RejectedExecutionException("command is not instance of MultiplexCallable");
        }

        RunnableFuture<T> runnableFuture = wrapTask((MultiplexCallable) task);
        execute(runnableFuture);
        return runnableFuture;
    }

    /**
     * {@inheritDoc}
     *
     * @param task must be instance of {@link MultiplexRunnable}
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        verifyRunnable(task);
        RunnableFuture<T> runnableFuture = wrapTask((MultiplexRunnable) task, result);
        execute(runnableFuture);
        return runnableFuture;
    }

    /**
     * {@inheritDoc}
     *
     * @param task must be instance of {@link MultiplexRunnable}
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    @Override
    public Future<?> submit(Runnable task) {
        verifyRunnable(task);
        RunnableFuture<Void> runnableFuture = wrapTask((MultiplexRunnable) task, null);
        execute(runnableFuture);
        return runnableFuture;
    }

    private void verifyRunnable(Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        if (!(task instanceof MultiplexRunnable)) {
            throw new RejectedExecutionException("command is not instance of MultiplexRunnable");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param command must be instance of {@link MultiplexRunnable}
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    @Override
    public void execute(Runnable command) {
        verifyRunnable(command);
        Executor executor = executorServiceSelector.select((MultiplexRunnable) command);
        if (executor == null) {
            throw new RejectedExecutionException("selected executor is null");
        }
        executor.execute(command);
    }
}
