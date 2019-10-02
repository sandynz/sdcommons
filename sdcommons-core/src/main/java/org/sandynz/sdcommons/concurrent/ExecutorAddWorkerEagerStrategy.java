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
 * Strategy implementation which is eager to add new worker.
 *
 * @author sandynz
 */
public class ExecutorAddWorkerEagerStrategy extends AbstractExecutorAddWorkerStrategy {

    @Override
    protected boolean addWorkerStep1(Runnable command, ExecutorExtContext ctx) {
        if (ctx.getWorkerCount() < ctx.getCorePoolSize()) {
            return ctx.addWorker(command, true);
        }
        int workerCount = ctx.getWorkerCount();
        if (workerCount < ctx.getMaximumPoolSize() && workerCount < ctx.getWorkQueue().size()) {
            // If work queue is SynchronousQueue which size is 0, steps are the same as ThreadPoolExecutor#execute
            return ctx.addWorker(command, false);
        }
        return false;
    }

}
