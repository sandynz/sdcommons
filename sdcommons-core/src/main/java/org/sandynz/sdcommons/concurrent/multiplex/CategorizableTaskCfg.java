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
package org.sandynz.sdcommons.concurrent.multiplex;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

/**
 * {@link CategorizableTask} configuration.
 *
 * @author sandynz
 */
@Data
@Accessors(chain = true)
@ToString
public class CategorizableTaskCfg {

    /**
     * Task category.
     * Could NOT be blank.
     */
    private String taskCategory;
    /**
     * Task priority.
     * Range: [1,10]. The greater the higher.
     */
    private int taskPriority;

    public void setTaskCategory(String taskCategory) {
        if (StringUtils.isBlank(taskCategory)) {
            throw new IllegalArgumentException("taskCategory blank");
        }
        this.taskCategory = taskCategory;
    }

    public void setTaskPriority(int taskPriority) {
        if (taskPriority < 1 || taskPriority > 10) {
            throw new IllegalArgumentException("invalid taskPriority=" + taskPriority);
        }
        this.taskPriority = taskPriority;
    }
}
