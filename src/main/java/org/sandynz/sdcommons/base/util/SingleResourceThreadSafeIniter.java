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
import java.util.function.Function;

/**
 * Thread-safe single resource initializer.
 *
 * @param <Input>  the input parameter type
 * @param <Result> the result type
 * @author sandynz
 */
public class SingleResourceThreadSafeIniter<Input, Result> {

    private final AtomicReference<Optional<Result>> resultAtomicReference = new AtomicReference<>();

    public Result initOnceAndGet(Input input, Function<Input, Result> resourceFunction) {
        return initOnceAndGet(input, resourceFunction, false);
    }

    @SuppressWarnings("OptionalAssignedToNull")
    public Result initOnceAndGet(Input input, Function<Input, Result> resourceFunction, boolean cacheNullResult) {
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
