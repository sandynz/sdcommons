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
package org.sandynz.sdcommons.base.time.range;

import java.time.LocalDateTime;
import java.util.Iterator;

/**
 * LocalDateTime range, usage:
 * </p>
 * for (LocalDateTime nextDay : new LocalDateTimeRange(startTime, endTime, TimeRangeIntervalUnit.DAY))
 *
 * @author sandynz
 */
public class LocalDateTimeRange implements Iterable<LocalDateTime> {

    private static final long serialVersionUID = 1L;

    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final TimeRangeIntervalUnit intervalUnit;

    /**
     * @param startTime    start time, inclusive
     * @param endTime      end time, inclusive
     * @param intervalUnit time range interval unit
     */
    public LocalDateTimeRange(LocalDateTime startTime, LocalDateTime endTime, TimeRangeIntervalUnit intervalUnit) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("null start or end time");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("start time is larger than end time");
        }
        if (intervalUnit == null) {
            throw new NullPointerException("null interval unit");
        }
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalUnit = intervalUnit;
    }

    /**
     * @return LocalDateTime iterator, it's not thread-safe.
     */
    @Override
    public Iterator<LocalDateTime> iterator() {
        return new LocalDateTimeRangeIterator();
    }

    private final class LocalDateTimeRangeIterator implements Iterator<LocalDateTime> {

        private LocalDateTime next;
        private boolean taken;

        private LocalDateTimeRangeIterator() {
            this.next = startTime;
            this.taken = false;
        }

        @Override
        public boolean hasNext() {
            LocalDateTime next = this.next;
            if (this.taken) {
                switch (intervalUnit) {
                    case YEAR:
                        next = next.plusYears(1);
                        break;
                    case MONTH:
                        next = next.plusMonths(1);
                        break;
                    case WEEK:
                        next = next.plusWeeks(1);
                        break;
                    case DAY:
                        next = next.plusDays(1);
                        break;
                    case HOUR:
                        next = next.plusHours(1);
                        break;
                    case MINUTE:
                        next = next.plusMinutes(1);
                        break;
                    case SECOND:
                        next = next.plusSeconds(1);
                        break;
                    default:
                        throw new IllegalArgumentException("unknown intervalType=" + intervalUnit);
                }

                this.next = next;
                this.taken = false;
            }

            return !next.isAfter(endTime);
        }

        @Override
        public LocalDateTime next() {
            if (!hasNext()) {
                return null;
            }
            this.taken = true;
            return this.next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
