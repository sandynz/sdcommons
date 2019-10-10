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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.sandynz.sdcommons.validation.Validations;

/**
 * {@link AdaptiveTask} configuration.
 *
 * @author sandynz
 */
@Data
@Accessors(chain = true)
@ToString
public class AdaptiveTaskCfg {

    public static final int PRIORITY_MIN = 1;
    public static final int PRIORITY_MAX = 10;
    public static final int PRIORITY_MID = 5;

    @Data
    @Accessors(chain = true)
    @ToString
    public static class Builder {

        @NotBlank
        private String taskCategory;
        @NotNull
        @Size(min = PRIORITY_MID, max = PRIORITY_MAX)
        private Integer taskPriority;
        @NotNull
        @Min(1)
        private Long upstreamTimeoutMillis;

        public AdaptiveTaskCfg build() {
            return new AdaptiveTaskCfg(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Task category.
     * Could NOT be blank.
     */
    private final String taskCategory;
    /**
     * Task priority.
     * Range: [1,10]. The greater the higher.
     */
    private final int taskPriority;
    /**
     * Upstream (e.g. gateway, micro-service invoker) timeout in millis.
     * Example values: 3000, 8000.
     */
    private final long upstreamTimeoutMillis;

    private AdaptiveTaskCfg(Builder builder) {
        boolean validateRet = Validations.validateBean(builder);
        if (!validateRet) {
            throw new IllegalArgumentException("invalid settings");
        }

        this.taskCategory = builder.taskCategory;
        this.taskPriority = builder.taskPriority;
        this.upstreamTimeoutMillis = builder.upstreamTimeoutMillis;
    }

}