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
package org.sandynz.sdcommons.concurrent.adaptive.internal;

import java.util.concurrent.FutureTask;
import org.sandynz.sdcommons.concurrent.adaptive.AdaptiveCallable;
import org.sandynz.sdcommons.concurrent.adaptive.AdaptiveRunnable;
import org.sandynz.sdcommons.concurrent.adaptive.AdaptiveTask;
import org.sandynz.sdcommons.concurrent.adaptive.AdaptiveTaskCfg;

/**
 * Adaptive {@linkplain FutureTask}.
 *
 * @param <V> result type returned by this {@code get} methods
 * @author sandynz
 */
public class AdaptiveFutureTask<V> extends FutureTask<V> implements AdaptiveRunnableFuture<V> {

    private final AdaptiveTask adaptiveTask;

    public AdaptiveFutureTask(AdaptiveCallable<V> callable) {
        super(callable);
        this.adaptiveTask = callable;
    }

    public AdaptiveFutureTask(AdaptiveRunnable runnable, V result) {
        super(runnable, result);
        this.adaptiveTask = runnable;
    }

    @Override
    public AdaptiveTaskCfg getAdaptiveTaskCfg() {
        return adaptiveTask.getAdaptiveTaskCfg();
    }
}
