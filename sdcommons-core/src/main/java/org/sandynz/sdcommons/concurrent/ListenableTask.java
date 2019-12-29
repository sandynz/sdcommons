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

import lombok.extern.slf4j.Slf4j;

/**
 * {@link Runnable} wrapper, listenable.
 *
 * @author sandynz
 */
@Slf4j
public class ListenableTask implements Runnable {

    private final Runnable runnable;
    private final RunnableListener[] runnableListeners;

    public ListenableTask(Runnable runnable, RunnableListener... runnableListeners) {
        this.runnable = runnable;
        this.runnableListeners = runnableListeners;
    }

    @Override
    public void run() {
        RunnableListener[] runnableListeners = this.runnableListeners;
        Object[] attachments = new Object[runnableListeners.length];
        int index = 0;
        for (RunnableListener listener : runnableListeners) {
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
        } catch (Throwable throwable) {
            index = 0;
            for (RunnableListener listener : runnableListeners) {
                try {
                    listener.afterExecute(this.runnable, throwable, attachments[index++]);
                } catch (Throwable e) {
                    log.error("afterExecute ex caught", e);
                    // ignore
                }
            }
            throw throwable;
        }
        index = 0;
        for (RunnableListener listener : runnableListeners) {
            try {
                listener.afterExecute(this.runnable, null, attachments[index++]);
            } catch (Throwable throwable) {
                log.error("afterExecute ex caught 2", throwable);
                // ignore
            }
        }
    }

}
