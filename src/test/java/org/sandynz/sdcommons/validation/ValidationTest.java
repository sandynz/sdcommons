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
package org.sandynz.sdcommons.validation;

import com.alibaba.fastjson.JSON;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

/**
 * All validation test cases.
 *
 * @author sandynz
 */
@Slf4j
public class ValidationTest {

    private void test0(List<Pair<ValidTestBean, Boolean>> paramResultPairList) {
        for (Pair<ValidTestBean, Boolean> paramResultPair : paramResultPairList) {
            ValidTestBean testBean = paramResultPair.getLeft();
            boolean expectedResult = paramResultPair.getRight();
            boolean validateResult = Validations.validateBean(testBean);
            log.info("testBean={}, expectedResult={}, validateResult={}", JSON.toJSONString(testBean), expectedResult, validateResult);
            Assert.assertEquals(expectedResult, validateResult);
        }
    }

    @Test
    public void testLooseCnMobile() {
        List<Pair<ValidTestBean, Boolean>> paramResultPairList = Arrays.asList(
                Pair.of(new ValidTestBean().setCnMobile("12345678"), false),
                Pair.of(new ValidTestBean().setCnMobile("23012345678"), false),
                Pair.of(new ValidTestBean().setCnMobile("13012345678"), true)
        );
        test0(paramResultPairList);
    }

    @Test
    public void testLooseCnTel() {
        List<Pair<ValidTestBean, Boolean>> paramResultPairList = Arrays.asList(
                Pair.of(new ValidTestBean().setCnTel("2301234567"), false),
                Pair.of(new ValidTestBean().setCnTel("010-12345678"), true),
                Pair.of(new ValidTestBean().setCnTel("0571-87654321"), true)
        );
        test0(paramResultPairList);
    }

    @Test
    public void testLooseCnMobileOrTel() {
        List<Pair<ValidTestBean, Boolean>> paramResultPairList = Arrays.asList(
                Pair.of(new ValidTestBean().setCnMobileOrTel("23012345678"), false),
                Pair.of(new ValidTestBean().setCnMobileOrTel("010-12345678"), true),
                Pair.of(new ValidTestBean().setCnMobileOrTel("0571-87654321"), true)
        );
        test0(paramResultPairList);
    }

    @Test
    public void testEqualsAnyInt() {
        List<Pair<ValidTestBean, Boolean>> paramResultPairList = Arrays.asList(
                Pair.of(new ValidTestBean().setType(3), false),
                Pair.of(new ValidTestBean().setType(1), true)
        );
        test0(paramResultPairList);
    }

    @Test
    public void testEqualsAnyString() {
        List<Pair<ValidTestBean, Boolean>> paramResultPairList = Arrays.asList(
                Pair.of(new ValidTestBean().setSex("F"), false),
                Pair.of(new ValidTestBean().setSex("Male"), true)
        );
        test0(paramResultPairList);
    }

    @Test
    public void testCnIdCardNo1() {
        List<Pair<ValidTestBean, Boolean>> paramResultPairList = Arrays.asList(
                Pair.of(new ValidTestBean().setCnIdCardNo1("530102192005080110"), false),
                Pair.of(new ValidTestBean().setCnIdCardNo1("530102199005080110"), false),
                Pair.of(new ValidTestBean().setCnIdCardNo1("53010219200508011x"), true),
                Pair.of(new ValidTestBean().setCnIdCardNo1("530102199005080111"), true)
        );
        test0(paramResultPairList);
    }

    @Test
    public void testCnIdCardNo2() {
        List<Pair<ValidTestBean, Boolean>> paramResultPairList = Arrays.asList(
                Pair.of(new ValidTestBean().setCnIdCardNo2("53010219200508011x"), false),
                Pair.of(new ValidTestBean().setCnIdCardNo2("530102199005080111"), true)
        );
        test0(paramResultPairList);
    }

    @Test
    public void testDailyFuture() {
        List<Pair<ValidTestBean, Boolean>> paramResultPairList = Arrays.asList(
                Pair.of(new ValidTestBean().setLocalDateDailyFuture(LocalDate.now().plusDays(-1)), false),
                Pair.of(new ValidTestBean().setLocalDateDailyFuture(LocalDate.now()), false),
                Pair.of(new ValidTestBean().setLocalDateDailyFuture(LocalDate.now().plusDays(1)), true),
                Pair.of(new ValidTestBean().setLocalDateTimeDailyFuture(LocalDateTime.now().plusDays(-1)), false),
                Pair.of(new ValidTestBean().setLocalDateTimeDailyFuture(LocalDateTime.now()), false),
                Pair.of(new ValidTestBean().setLocalDateTimeDailyFuture(LocalDateTime.now().plusDays(1)), true),
                Pair.of(new ValidTestBean().setDateDailyFuture(DateUtils.addDays(new Date(), -1)), false),
                Pair.of(new ValidTestBean().setDateDailyFuture(new Date()), false),
                Pair.of(new ValidTestBean().setDateDailyFuture(DateUtils.addDays(new Date(), 1)), true)
        );
        test0(paramResultPairList);
    }

    @Test
    public void testFutureOrPresent() {
        List<Pair<ValidTestBean, Boolean>> paramResultPairList = Arrays.asList(
                Pair.of(new ValidTestBean().setDateFutureOrPresent(DateUtils.addDays(new Date(), -1)), false),
                Pair.of(new ValidTestBean().setDateFutureOrPresent(new Date()), false),
                Pair.of(new ValidTestBean().setDateFutureOrPresent(DateUtils.addSeconds(new Date(), 5)), true),
                Pair.of(new ValidTestBean().setDateFutureOrPresent(DateUtils.addDays(new Date(), 1)), true)
        );
        test0(paramResultPairList);
    }

}
