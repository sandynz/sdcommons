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

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
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

    private final ConcurrentMap<Key, AtomicReference<Optional<Result>>> resultMap = new ConcurrentHashMap<>(16, 0.5F);

    /**
     * Get resource, init resource if necessary.
     * <p>
     * Cache resource if it is null. Just init resource once globally for every key.
     *
     * @param key              map key related to resource
     * @param input            parameter used for {@code resourceFunction}
     * @param resourceFunction function to init resource
     */
    public Result initOnceAndGet(Key key, Input input, Function<Input, Result> resourceFunction) {
        return initAndGet(key, input, resourceFunction, true);
    }

    /**
     * Get resource, init resource if necessary.
     * <p>
     * Just init once globally for every key when {@code cacheNullResult} is true.
     *
     * @param key              map key related to resource
     * @param input            parameter used for {@code resourceFunction}
     * @param resourceFunction function to init resource
     * @param cacheNullResult  whether cache null resource or not
     */
    @SuppressWarnings({"OptionalAssignedToNull", "SynchronizationOnLocalVariableOrMethodParameter"})
    public Result initAndGet(Key key, Input input, Function<Input, Result> resourceFunction, boolean cacheNullResult) {
        AtomicReference<Optional<Result>> resultAtomicReference = resultMap.get(key);
        if (resultAtomicReference == null) {
            resultAtomicReference = new AtomicReference<>();
            AtomicReference<Optional<Result>> oldValue = resultMap.putIfAbsent(key, resultAtomicReference);
            log.info("resultAtomicReference null, key={}, oldValueIsNull={}", key, (oldValue == null));
            if (oldValue != null) {
                resultAtomicReference = oldValue;
            }
        }

        Optional<Result> optionalResult = resultAtomicReference.get();
        if (optionalResult == null) {
            synchronized (resultAtomicReference) {
                optionalResult = resultAtomicReference.get();
                if (optionalResult == null) {
                    Result result = resourceFunction.apply(input);
                    if (result != null || cacheNullResult) {
                        optionalResult = Optional.ofNullable(result);
                        resultAtomicReference.set(optionalResult);
                        return optionalResult.orElse(null);
                    } else {
                        return null;
                    }
                } else {
                    return optionalResult.orElse(null);
                }
            }
        } else {
            return optionalResult.orElse(null);
        }
    }

}
