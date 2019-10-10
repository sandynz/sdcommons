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
import lombok.extern.slf4j.Slf4j;
import org.sandynz.sdcommons.concurrent.adaptive.internal.AdaptiveFutureTask;
import org.sandynz.sdcommons.concurrent.adaptive.internal.AdaptiveRunnableFuture;

/**
 * Adaptive {@link ExecutorService}.
 * TODO
 *
 * @author sandynz
 */
@Slf4j
public class AdaptiveExecutorService extends AbstractExecutorService implements AdaptiveRunnableListener {

    private final ExecutorServiceSelector executorServiceSelector;

    public AdaptiveExecutorService(ExecutorServiceSelector executorServiceSelector) {
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

    private <T> AdaptiveRunnableFuture<T> wrapTask(AdaptiveRunnable runnable, T value) {
        return new AdaptiveFutureTask<>(runnable, value);
    }

    private <T> AdaptiveRunnableFuture<T> wrapTask(AdaptiveCallable<T> callable) {
        return new AdaptiveFutureTask<>(callable);
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
        if (!(task instanceof AdaptiveCallable)) {
            throw new RejectedExecutionException("command is not instance of AdaptiveCallable");
        }

        RunnableFuture<T> runnableFuture = wrapTask((AdaptiveCallable<T>) task);
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
        RunnableFuture<T> runnableFuture = wrapTask((AdaptiveRunnable) task, result);
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
        RunnableFuture<Void> runnableFuture = wrapTask((AdaptiveRunnable) task, null);
        execute(runnableFuture);
        return runnableFuture;
    }

    private void verifyRunnable(Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        if (!(task instanceof AdaptiveRunnable)) {
            throw new RejectedExecutionException("command is not instance of AdaptiveRunnable");
        }
    }

    private static class ListenableTask implements Runnable {

        private final AdaptiveRunnable runnable;
        private final AdaptiveRunnableListener[] runnableListeners;

        ListenableTask(AdaptiveRunnable runnable, AdaptiveRunnableListener... runnableListeners) {
            this.runnable = runnable;
            this.runnableListeners = runnableListeners;
        }

        @Override
        public void run() {
            AdaptiveRunnableListener[] runnableListeners = this.runnableListeners;
            Object[] attachments = new Object[runnableListeners.length];
            int index = 0;
            for (AdaptiveRunnableListener listener : runnableListeners) {
                Object beforeRet;
                try {
                    beforeRet = listener.beforeExecute(Thread.currentThread(), this.runnable);
                } catch (Throwable throwable) {
                    beforeRet = throwable;
                }
                attachments[index++] = beforeRet;
            }
            try {
                this.runnable.run();
                index = 0;
                for (AdaptiveRunnableListener listener : runnableListeners) {
                    listener.afterExecute(this.runnable, null, attachments[index++]);
                }
            } catch (Throwable throwable) {
                index = 0;
                for (AdaptiveRunnableListener listener : runnableListeners) {
                    try {
                        listener.afterExecute(this.runnable, throwable, attachments[index++]);
                    } catch (Throwable e) {
                        log.error("afterExecute ex caught", e);
                        // ignore
                    }
                }
                throw throwable;
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param command must be instance of {@link AdaptiveRunnable}
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    @Override
    public void execute(Runnable command) {
        verifyRunnable(command);
        Executor executor = this.executorServiceSelector.select((AdaptiveRunnable) command);
        if (executor == null) {
            throw new RejectedExecutionException("selected executor is null");
        }
        executor.execute(new ListenableTask((AdaptiveRunnable) command, this, this.executorServiceSelector));
    }

}
