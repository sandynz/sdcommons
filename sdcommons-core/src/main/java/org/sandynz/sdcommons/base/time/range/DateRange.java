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

import java.util.Date;
import java.util.Iterator;
import org.apache.commons.lang3.time.DateUtils;

/**
 * Date range, usage:
 * </p>
 * for (Date nextDay : new DateRange(startTime, endTime, TimeRangeIntervalUnit.DAY))
 *
 * @author sandynz
 */
public class DateRange implements Iterable<Date> {

    private static final long serialVersionUID = 1L;

    private final Date startTime;
    private final Date endTime;
    private final TimeRangeIntervalUnit intervalUnit;

    /**
     * @param startTime    start time, inclusive
     * @param endTime      end time, inclusive
     * @param intervalUnit time range interval unit
     */
    public DateRange(Date startTime, Date endTime, TimeRangeIntervalUnit intervalUnit) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("null start or end time");
        }
        if (startTime.after(endTime)) {
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
     * @return Date iterator, it's not thread-safe.
     */
    @Override
    public Iterator<Date> iterator() {
        return new DateRangeIterator();
    }

    private final class DateRangeIterator implements Iterator<Date> {

        private Date next;
        private boolean taken;

        private DateRangeIterator() {
            this.next = startTime;
            this.taken = false;
        }

        @Override
        public boolean hasNext() {
            Date next = this.next;
            if (this.taken) {
                switch (intervalUnit) {
                    case YEAR:
                        next = DateUtils.addYears(next, 1);
                        break;
                    case MONTH:
                        next = DateUtils.addMonths(next, 1);
                        break;
                    case WEEK:
                        next = DateUtils.addWeeks(next, 1);
                        break;
                    case DAY:
                        next = DateUtils.addDays(next, 1);
                        break;
                    case HOUR:
                        next = DateUtils.addHours(next, 1);
                        break;
                    case MINUTE:
                        next = DateUtils.addMinutes(next, 1);
                        break;
                    case SECOND:
                        next = DateUtils.addSeconds(next, 1);
                        break;
                    default:
                        throw new IllegalArgumentException("unknown intervalType=" + intervalUnit);
                }

                this.next = next;
                this.taken = false;
            }

            return !next.after(endTime);
        }

        @Override
        public Date next() {
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
