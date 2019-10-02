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

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple {@link ThreadFactory} implementation, could be used to customize thread name prefix.
 *
 * @author sandynz
 */
public class ThreadFactoryImpl implements ThreadFactory {

    private final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();

    private final String namePrefix;
    private final boolean daemon;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public ThreadFactoryImpl(String namePrefix) {
        this(namePrefix, false);
    }

    public ThreadFactoryImpl(String namePrefix, boolean daemon) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = this.defaultThreadFactory.newThread(r);
        thread.setDaemon(this.daemon);
        thread.setName(this.namePrefix + this.threadNumber.getAndIncrement());
        return thread;
    }

}
