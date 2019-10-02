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

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;
import org.sandynz.sdcommons.base.time.JodaDateTimeFormatters;

/**
 * JodaDateTimeRange test cases.
 *
 * @author sandynz
 */
@Slf4j
public class JodaDateTimeRangeTest {

    @Data
    @Accessors(chain = true)
    @ToString
    private static class TestCfg {

        String startTimeStr;
        String endTimeStr;
        DateTimeFormatter dateTimeFormatter;
        TimeRangeIntervalUnit intervalUnit;
        int expectedCount;

    }

    private void test0(TestCfg cfg) {
        log.info("------\nstart, cfg={}", cfg);
        DateTimeFormatter dateTimeFormatter = cfg.dateTimeFormatter;
        List<DateTime> dateTimeList = new ArrayList<>();
        for (DateTime next : new JodaDateTimeRange(
                dateTimeFormatter.parseDateTime(cfg.startTimeStr),
                dateTimeFormatter.parseDateTime(cfg.endTimeStr),
                cfg.intervalUnit)) {
            log.info("next {} = {}", cfg.intervalUnit, dateTimeFormatter.print(next));
            dateTimeList.add(next);
        }
        Assert.assertEquals(cfg.expectedCount, dateTimeList.size());
        Assert.assertEquals(cfg.startTimeStr, dateTimeFormatter.print(dateTimeList.get(0)));
        Assert.assertEquals(cfg.endTimeStr, dateTimeFormatter.print(dateTimeList.get(dateTimeList.size() - 1)));
    }

    @Test
    public void testYearRange() {
        TestCfg cfg = new TestCfg()
                .setStartTimeStr("2017").setEndTimeStr("2019")
                .setDateTimeFormatter(JodaDateTimeFormatters.yyyy)
                .setIntervalUnit(TimeRangeIntervalUnit.YEAR)
                .setExpectedCount(3);
        test0(cfg);
        cfg.setEndTimeStr(cfg.getStartTimeStr()).setExpectedCount(1);
        test0(cfg);
    }

    @Test
    public void testMonthRange() {
        TestCfg cfg = new TestCfg()
                .setStartTimeStr("2019-01").setEndTimeStr("2019-03")
                .setDateTimeFormatter(JodaDateTimeFormatters.yyyyMM)
                .setIntervalUnit(TimeRangeIntervalUnit.MONTH)
                .setExpectedCount(3);
        test0(cfg);
        cfg.setEndTimeStr(cfg.getStartTimeStr()).setExpectedCount(1);
        test0(cfg);
    }

    @Test
    public void testDayRange() {
        TestCfg cfg = new TestCfg()
                .setStartTimeStr("2019-09-26").setEndTimeStr("2019-09-28")
                .setDateTimeFormatter(JodaDateTimeFormatters.yyyyMMdd)
                .setIntervalUnit(TimeRangeIntervalUnit.DAY)
                .setExpectedCount(3);
        test0(cfg);
        cfg.setEndTimeStr(cfg.getStartTimeStr()).setExpectedCount(1);
        test0(cfg);
    }

    @Test
    public void testHourRange() {
        TestCfg cfg = new TestCfg()
                .setStartTimeStr("2019-09-26 21").setEndTimeStr("2019-09-26 23")
                .setDateTimeFormatter(JodaDateTimeFormatters.yyyyMMddHH)
                .setIntervalUnit(TimeRangeIntervalUnit.HOUR)
                .setExpectedCount(3);
        test0(cfg);
        cfg.setEndTimeStr(cfg.getStartTimeStr()).setExpectedCount(1);
        test0(cfg);
    }

}
