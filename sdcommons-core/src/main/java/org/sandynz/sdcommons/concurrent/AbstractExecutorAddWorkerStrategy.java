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

import org.sandynz.sdcommons.concurrent.ThreadPoolExecutor.ExecutorExtContext;

/**
 * Abstract implementation of {@linkplain ExecutorAddWorkerStrategy}.
 * Keep step 2 and 3 the same as {@linkplain ThreadPoolExecutor#execute(Runnable)},
 * and step 1 could be customized.
 *
 * @author sandynz
 */
public abstract class AbstractExecutorAddWorkerStrategy implements ExecutorAddWorkerStrategy {

    /**
     * Returns whether worker added or not.
     *
     * @return true if worker added (no more steps are needed), else false (need to do next steps).
     */
    protected abstract boolean addWorkerStep1(Runnable command, ExecutorExtContext ctx);

    @Override
    public void addWorker(Runnable command, ExecutorExtContext ctx) {
        boolean step1Ret = addWorkerStep1(command, ctx);
        if (step1Ret) {
            return;
        }

        // Step 2 and 3. The same as ThreadPoolExecutor#execute
        if (ctx.isRunning() && ctx.getWorkQueue().offer(command)) {
            if (!ctx.isRunning() && ctx.removeTask(command)) {
                ctx.rejectTask(command);
            } else if (ctx.getWorkerCount() == 0) {
                ctx.addWorker(null, false);
            }
        } else if (!ctx.addWorker(command, false)) {
            ctx.rejectTask(command);
        }
    }
}
