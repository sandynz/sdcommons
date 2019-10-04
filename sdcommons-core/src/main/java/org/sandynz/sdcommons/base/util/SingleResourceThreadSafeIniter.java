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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;

/**
 * Thread-safe single resource initializer.
 *
 * @param <Input>  the input parameter type
 * @param <Result> the result type
 * @author sandynz
 */
@Slf4j
public class SingleResourceThreadSafeIniter<Input, Result> {

    private final AtomicReference<Optional<Result>> resultAtomicReference = new AtomicReference<>();

    private volatile boolean resourceCleaned = false;

    boolean isResourceCleaned() {
        return resourceCleaned;
    }

    /**
     * Get resource, init or re-init resource if necessary.
     * <p>
     * Cache resource if it is null. Just init resource once globally.
     *
     * @see #initAndGet(Object, BiFunction, Predicate, Predicate)
     */
    public Result initOnceAndGet(
            Input input,
            BiFunction<Input, Result/*OldResult*/, Result> resourceFunction
    ) {
        return initAndGet(input, resourceFunction, result -> true, null);
    }

    /**
     * Get resource, init or re-init resource if necessary.
     *
     * @param input                    parameter used for {@code resourceFunction}. Could be null.
     * @param resourceFunction         function to init resource, re-init resource, destroy expired resource. If new resource created after old resource expired and could not be cached, then resource will be cleaned. Could NOT be null.
     * @param cacheResourcePredicate   indicate whether to cache resource or not by predicated result. Could NOT be null.
     * @param resourceExpiredPredicate indicate whether the resource is expired or not by predicated result, if result is true, then re-init resource. Could be null.
     * @return resource or null
     * @throws NullPointerException if resourceFunction / cacheResourcePredicate is null
     */
    @SuppressWarnings("OptionalAssignedToNull")
    public Result initAndGet(
            Input input,
            BiFunction<Input, Result/*OldResult*/, Result> resourceFunction,
            Predicate<Result> cacheResourcePredicate,
            Predicate<Result> resourceExpiredPredicate
    ) {
        if (resourceFunction == null || cacheResourcePredicate == null) {
            throw new NullPointerException();
        }
        Optional<Result> optionalResult = resultAtomicReference.get();
        if (optionalResult == null) {
            synchronized (resultAtomicReference) {
                optionalResult = resultAtomicReference.get();
                if (optionalResult == null) {
                    Result result = resourceFunction.apply(input, null);
                    if (cacheResourcePredicate.test(result)) {
                        optionalResult = Optional.ofNullable(result);
                        resultAtomicReference.set(optionalResult);
                        resourceCleaned = false;
                        return result;
                    } else {
                        return null;
                    }
                }
            }
        }
        Result result = optionalResult.orElse(null);
        if (resourceExpiredPredicate != null && resourceExpiredPredicate.test(result)) {
            synchronized (resultAtomicReference) {
                optionalResult = resultAtomicReference.get();
                result = optionalResult.orElse(null);
                if (resourceExpiredPredicate.test(result)) {
                    log.info("resource expired, input={}", input);
                    result = resourceFunction.apply(input, result);
                    if (cacheResourcePredicate.test(result)) {
                        optionalResult = Optional.ofNullable(result);
                        resultAtomicReference.set(optionalResult);
                        resourceCleaned = false;
                        return result;
                    } else {
                        log.warn("resource expired, but re-inited resource could not be cached");
                        // if it could not be cached, clean expired resource
                        resultAtomicReference.set(null);
                        resourceCleaned = true;
                        return null;
                    }
                }
            }
        }
        return result;
    }

}
