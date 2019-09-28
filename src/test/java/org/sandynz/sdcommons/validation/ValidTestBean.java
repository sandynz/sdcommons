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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import javax.validation.constraints.FutureOrPresent;
import lombok.Data;
import lombok.experimental.Accessors;
import org.sandynz.sdcommons.validation.constraints.CnIdCardNo;
import org.sandynz.sdcommons.validation.constraints.DailyFuture;
import org.sandynz.sdcommons.validation.constraints.DailyFutureOrPresent;
import org.sandynz.sdcommons.validation.constraints.DailyPast;
import org.sandynz.sdcommons.validation.constraints.DailyPastOrPresent;
import org.sandynz.sdcommons.validation.constraints.EqualsAnyInt;
import org.sandynz.sdcommons.validation.constraints.EqualsAnyString;
import org.sandynz.sdcommons.validation.constraints.LooseCnMobile;
import org.sandynz.sdcommons.validation.constraints.LooseCnTel;
import org.sandynz.sdcommons.validation.constraints.hibernate.LooseCnMobileOrTel;

/**
 * Test class for validation.
 *
 * @author sandynz
 */
@Data
@Accessors(chain = true)
class ValidTestBean {

    @LooseCnMobile
    private String cnMobile;

    @LooseCnTel
    private String cnTel;

    @LooseCnMobileOrTel
    private String cnMobileOrTel;

    @EqualsAnyInt(value = {1, 2}, message = "Must be one of 1 / 2")
    private Integer type1;

    @EqualsAnyInt(integerAlternativesGetter = SexEnumIdExtractor.class)
    private Integer type2;

    @EqualsAnyString(value = {"MALE", "FEMALE"}, message = "Must be one of MALE / FEMALE")
    private String sex1;

    @EqualsAnyString(stringAlternativesGetter = SexEnumNameExtractor.class)
    private String sex2;

    @CnIdCardNo
    private String cnIdCardNo1;

    @CnIdCardNo(minAge = 18, maxAge = 65)
    private String cnIdCardNo2;

    @DailyFuture
    private LocalDate localDateDailyFuture;
    @DailyFuture
    private LocalDateTime localDateTimeDailyFuture;
    @DailyFuture
    private Date dateDailyFuture;

    @FutureOrPresent
    private Date dateFutureOrPresent;

    @DailyFutureOrPresent
    private LocalDate localDateDailyFutureOrPresent;
    @DailyFutureOrPresent
    private LocalDateTime localDateTimeDailyFutureOrPresent;
    @DailyFutureOrPresent
    private Date dateDailyFutureOrPresent;

    @DailyPast
    private LocalDate localDateDailyPast;
    @DailyPast
    private LocalDateTime localDateTimeDailyPast;
    @DailyPast
    private Date dateDailyPast;

    @DailyPastOrPresent
    private LocalDate localDateDailyPastOrPresent;
    @DailyPastOrPresent
    private LocalDateTime localDateTimeDailyPastOrPresent;
    @DailyPastOrPresent
    private Date dateDailyPastOrPresent;

}
