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
package org.sandynz.sdcommons.validation.constraintvalidators.time.past;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * DailyPast utility for validator implementations.
 *
 * @author sandynz
 */
class DailyPastUtils {

    private static long getTodayStartMillis() {
        return LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    static boolean isBeforeToday(LocalDate value) {
        return value.atTime(LocalTime.NOON).toInstant(ZoneOffset.UTC).toEpochMilli() < getTodayStartMillis();
    }

    static boolean isBeforeToday(LocalDateTime value) {
        return value.toInstant(ZoneOffset.UTC).toEpochMilli() < getTodayStartMillis();
    }

    static boolean isBeforeToday(Date value) {
        return value.getTime() < getTodayStartMillis();
    }

}
