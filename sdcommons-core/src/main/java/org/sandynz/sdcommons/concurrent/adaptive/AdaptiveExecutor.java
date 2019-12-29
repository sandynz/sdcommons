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

import lombok.extern.slf4j.Slf4j;
import org.sandynz.sdcommons.concurrent.ExecutorAddWorkerEagerStrategy;
import org.sandynz.sdcommons.concurrent.ExecutorAddWorkerStrategy;
import org.sandynz.sdcommons.concurrent.ExecutorConstructionCfg;
import org.sandynz.sdcommons.concurrent.ExtendedThreadPoolExecutor;

/**
 * TODO
 *
 * @author sandynz
 */
@Slf4j
public class AdaptiveExecutor extends ExtendedThreadPoolExecutor {

    /**
     * @param cfg   {@code workQueue} should not be set; {@code addWorkerStrategy} is alternative;
     * @param queue used as {@code workQueue}
     */
    public AdaptiveExecutor(ExecutorConstructionCfg cfg, AdaptiveDualBlockingQueue queue) {
        super(cfg.getCorePoolSize(), cfg.getMaxPoolSize(), cfg.getKeepAliveTime(), cfg.getUnit(), queue, cfg.getThreadFactory(), cfg.getHandler());

        if (cfg.getCorePoolSize() == cfg.getMaxPoolSize()) {
            throw new IllegalArgumentException("corePoolSize equals to maxPoolSize");
        }

        ExecutorAddWorkerStrategy addWorkerStrategy = cfg.getAddWorkerStrategy();
        if (addWorkerStrategy == null) {
            addWorkerStrategy = new ExecutorAddWorkerEagerStrategy();
        }
        this.setAddWorkerStrategy(addWorkerStrategy);
    }

    //boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;
    // forbid allowCoreThreadTimeOut
    @Override
    public void allowCoreThreadTimeOut(boolean value) {
        throw new UnsupportedOperationException();
    }

}
