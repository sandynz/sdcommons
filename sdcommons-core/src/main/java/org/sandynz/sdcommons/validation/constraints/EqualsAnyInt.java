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
package org.sandynz.sdcommons.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;
import javax.validation.Constraint;
import javax.validation.Payload;
import org.sandynz.sdcommons.validation.constraintvalidators.EqualsAnyIntValidator;

/**
 * The annotated element must equals to one of the specified int.
 *
 * @author sandynz
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = {EqualsAnyIntValidator.class})
@Documented
@Repeatable(EqualsAnyInt.List.class)
public @interface EqualsAnyInt {

    String message() default "{org.sandynz.sdcommons.validation.constraints.EqualsAnyInt.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Integer alternatives.
     * <p>
     * Optional.
     * <p>
     * First priority if defined.
     */
    int[] value() default {};

    /**
     * Integer alternatives could be got from {@link EqualsAnyInt.IntegerAlternativesGetter}.
     * <p>
     * Optional.
     * <p>
     * Secondly priority if {@link #value()} defined.
     */
    Class<? extends EqualsAnyInt.IntegerAlternativesGetter>[] integerAlternativesGetter() default {};

    interface IntegerAlternativesGetter {

        Collection<Integer> getIntegerAlternatives();

    }

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        EqualsAnyInt[] value();
    }

}
