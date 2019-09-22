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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.sandynz.validation.constraints.CnIdCardNo;
import org.sandynz.validation.constraints.EqualsAnyString;
import org.sandynz.validation.util.CnIdCardNoBean;
import org.sandynz.validation.util.CnIdCardNoUtils;

/**
 * 中国居民身份证号码校验器
 *
 * @author sandynz
 */
@Slf4j
public class CnIdCardNoValidator implements ConstraintValidator<CnIdCardNo, String> {

    public CnIdCardNoValidator() {
    }

    private int minAge;
    private int maxAge;

    @Override
    public void initialize(CnIdCardNo constraintAnnotation) {
        minAge = constraintAnnotation.minAge();
        maxAge = constraintAnnotation.maxAge();
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        if (object == null) {
            return true;
        }

        CnIdCardNoBean bean = CnIdCardNoUtils.parseCnIdCardNo(object);
        if (bean == null) {
            return false;
        }

        Date birthDate;
        try {
            birthDate = new SimpleDateFormat("yyyyMMdd").parse(bean.getBirthday());
        } catch (ParseException e) {
            log.error("parse date failed, should be impossible", e);
            return false;
        }
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate now = LocalDate.now(zoneId);
        LocalDate birthLocalDate = birthDate.toInstant().atZone(zoneId).toLocalDate();
        int age = now.getYear() - birthLocalDate.getYear();
        if (now.compareTo(birthLocalDate.plusYears(age).plusDays(1)) >= 0) {
            age += 1;
        }
        return minAge <= age && age <= maxAge;
    }
}
