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

/**
 * {@linkplain MultiplexRunnable} listener.
 *
 * @author sandynz
 */
public interface MultiplexRunnableListener {

    /**
     * Invoked at the beginning of {@code runnable}.
     *
     * @param thread   the thread that will run task {@code runnable}
     * @param runnable the task that will be executed
     * @return attachment which will be a parameter of {@linkplain #afterExecute(MultiplexRunnable, Throwable, Object)}
     */
    default Object beforeExecute(Thread thread, MultiplexRunnable runnable) {
        return null;
    }

    /**
     * Invoked at the end of {@code runnable}.
     *
     * @param runnable                task
     * @param throwable               caught exception from {@link Runnable#run()}, may be null
     * @param beforeExecuteAttachment attachment from {@linkplain #beforeExecute(Thread, MultiplexRunnable)}. May be exception caught from {@code beforeExecute}.
     */
    default void afterExecute(MultiplexRunnable runnable, Throwable throwable, Object beforeExecuteAttachment) {
    }

}
