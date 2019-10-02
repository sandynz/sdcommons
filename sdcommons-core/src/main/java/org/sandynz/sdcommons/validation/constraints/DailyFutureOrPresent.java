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
import javax.validation.Constraint;
import javax.validation.Payload;
import org.sandynz.sdcommons.validation.constraintvalidators.time.futureorpresent.DailyFutureOrPresentValidatorForDate;
import org.sandynz.sdcommons.validation.constraintvalidators.time.futureorpresent.DailyFutureOrPresentValidatorForLocalDate;
import org.sandynz.sdcommons.validation.constraintvalidators.time.futureorpresent.DailyFutureOrPresentValidatorForLocalDateTime;

/**
 * The annotated element represents today or later.
 * <p>
 * Similar to {@link javax.validation.constraints.FutureOrPresent}, except:
 * <ul>
 *     <li>Measure by day.</li>
 *     <li>Just support {@link java.util.Date}, {@link java.time.LocalDate}, {@link java.time.LocalDateTime}.</li>
 * </ul>
 *
 * @author sandynz
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = {
        DailyFutureOrPresentValidatorForLocalDate.class,
        DailyFutureOrPresentValidatorForLocalDateTime.class,
        DailyFutureOrPresentValidatorForDate.class
})
@Documented
@Repeatable(DailyFutureOrPresent.List.class)
public @interface DailyFutureOrPresent {

    String message() default "{org.sandynz.sdcommons.validation.constraints.DailyFutureOrPresent.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        DailyFutureOrPresent[] value();
    }

}
