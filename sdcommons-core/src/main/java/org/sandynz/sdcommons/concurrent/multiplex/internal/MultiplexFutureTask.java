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
package org.sandynz.sdcommons.concurrent.multiplex.internal;

import java.util.concurrent.FutureTask;
import org.sandynz.sdcommons.concurrent.multiplex.CategorizableTask;
import org.sandynz.sdcommons.concurrent.multiplex.MultiplexCallable;
import org.sandynz.sdcommons.concurrent.multiplex.MultiplexRunnable;

/**
 * Multiplex {@linkplain FutureTask}.
 *
 * @param <V> result type returned by this {@code get} methods
 * @author sandynz
 */
public class MultiplexFutureTask<V> extends FutureTask<V> implements MultiplexRunnableFuture<V> {

    private final CategorizableTask taskCategorizable;

    public MultiplexFutureTask(MultiplexCallable<V> callable) {
        super(callable);
        this.taskCategorizable = callable;
    }

    public MultiplexFutureTask(MultiplexRunnable runnable, V result) {
        super(runnable, result);
        this.taskCategorizable = runnable;
    }

    @Override
    public String getTaskCategory() {
        return taskCategorizable.getTaskCategory();
    }
}
