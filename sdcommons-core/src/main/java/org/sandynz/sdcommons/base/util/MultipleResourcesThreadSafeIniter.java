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
package org.sandynz.sdcommons.base.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;

/**
 * Thread-safe multiple resources initializer.
 *
 * @param <Key>    cache key type of result map
 * @param <Input>  the input parameter type
 * @param <Result> the result type
 * @author sandynz
 */
@Slf4j
public class MultipleResourcesThreadSafeIniter<Key, Input, Result> {

    private final ConcurrentMap<Key, SingleResourceThreadSafeIniter<Input, Result>> resultMap = new ConcurrentHashMap<>(16, 0.5F);

    /**
     * Get resource, init or re-init resource if necessary.
     * <p>
     * Cache resource if it is null. Just init resource once globally for every key.
     *
     * @see #initAndGet(Object, Object, BiFunction, Predicate, Predicate)
     */
    public Result initOnceAndGet(
            Key key, Input input,
            BiFunction<Input, Result/*OldResult*/, Result> resourceFunction
    ) {
        return initAndGet(key, input, resourceFunction, result -> true, null);
    }

    /**
     * Get resource, init or re-init resource if necessary.
     *
     * @param key                      map key related to resource. Could NOT be null.
     * @param input                    parameter used for {@code resourceFunction}. Could be null.
     * @param resourceFunction         function to init resource. Could NOT be null.
     * @param cacheResourcePredicate   indicate whether to cache resource or not by predicated result. Could NOT be null.
     * @param resourceExpiredPredicate indicate whether the resource is expired or not by predicated result, if result is true, then re-init resource. Could be null.
     * @return resource or null
     * @throws NullPointerException if key / resourceFunction / cacheResourcePredicate is null
     */
    public Result initAndGet(
            Key key, Input input,
            BiFunction<Input, Result/*OldResult*/, Result> resourceFunction,
            Predicate<Result> cacheResourcePredicate,
            Predicate<Result> resourceExpiredPredicate
    ) {
        if (key == null || resourceFunction == null || cacheResourcePredicate == null) {
            throw new NullPointerException();
        }
        SingleResourceThreadSafeIniter<Input, Result> singleResourceIniter = resultMap.get(key);
        if (singleResourceIniter == null) {
            singleResourceIniter = new SingleResourceThreadSafeIniter<>();
            SingleResourceThreadSafeIniter<Input, Result> oldValue = resultMap.putIfAbsent(key, singleResourceIniter);
            log.info("singleResourceIniter null, key={}, oldValueIsNull={}", key, (oldValue == null));
            if (oldValue != null) {
                singleResourceIniter = oldValue;
            }
        }
        return singleResourceIniter.initAndGet(input, resourceFunction, cacheResourcePredicate, resourceExpiredPredicate);
    }

}
