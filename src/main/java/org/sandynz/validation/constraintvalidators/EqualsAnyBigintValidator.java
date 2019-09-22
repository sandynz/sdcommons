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
package org.sandynz.validation.constraintvalidators;

import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.sandynz.validation.constraints.EqualsAnyBigint;

/**
 * Validate the object equals to any of specified bigint.
 *
 * @author sandynz
 */
public class EqualsAnyBigintValidator implements ConstraintValidator<EqualsAnyBigint, Long> {

    public EqualsAnyBigintValidator() {
    }

    private Set<Long> valueSet;

    @Override
    public void initialize(EqualsAnyBigint constraintAnnotation) {
        long[] valueArr = constraintAnnotation.value();
        if (valueArr.length == 0) {
            valueSet = null;
        } else {
            valueSet = new HashSet<>(valueArr.length * 4 / 3 + 1);
            for (long value : valueArr) {
                valueSet.add(value);
            }
        }
    }

    @Override
    public boolean isValid(Long object, ConstraintValidatorContext constraintContext) {
        if (object == null) {
            return true;
        }

        return valueSet.contains(object);
    }
}
