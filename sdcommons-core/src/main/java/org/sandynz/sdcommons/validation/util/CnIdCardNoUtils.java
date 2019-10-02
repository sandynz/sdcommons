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
package org.sandynz.sdcommons.validation.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 中国居民身份证号码 解析工具类
 * <p>
 * 参考<a href="https://baike.sogou.com/v7481905.htm">居民身份证号码</a>
 *
 * @author sandynz
 */
public class CnIdCardNoUtils {

    private static final Pattern PATTERN_CN_ID_CARD_NO = Pattern.compile("^"
            // 六位地址码
            + "([1-9][0-9]{5})"
            // 年 + 月 + 日
            + "([1-9][0-9]{3})" + "([0-9]{2})" + "([0-9]{2})"
            // 三位数字顺序码
            + "([0-9]{3})"
            // 一位校验码
            + "([1-9Xx])"
            + "$");

    // 将前面的身份证号码17位数分别乘以不同的系数
    private static final int[] MULTIPLYING_FACTOR_ARR = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    // 余数[0-10]对应的校验码
    private static final char[] MOD_CHECK_CODE_ARR = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    /**
     * 解析中国居民身份证号码，限18位。
     *
     * @return null代表解析失败，否则是解析好的对象
     */
    public static CnIdCardNoBean parseCnIdCardNo(String cnIdCardNo) {
        if (cnIdCardNo == null || cnIdCardNo.isEmpty()) {
            return null;
        }
        Matcher matcher = PATTERN_CN_ID_CARD_NO.matcher(cnIdCardNo);
        if (!matcher.matches()) {
            return null;
        }
        int group = 0;
        CnIdCardNoBean result = new CnIdCardNoBean()
                .setAreaCode(matcher.group(++group))
                .setYear(Integer.parseInt(matcher.group(++group)))
                .setMonth(Integer.parseInt(matcher.group(++group)))
                .setDay(Integer.parseInt(matcher.group(++group)))
                .setSeqCode(matcher.group(++group))
                .setCheckCode(matcher.group(++group));

        // 最后一位校验
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (MULTIPLYING_FACTOR_ARR[i] * (cnIdCardNo.charAt(i) - '0'));
        }
        char checkCode = result.getCheckCode().charAt(0);
        if (checkCode == 'x') {
            checkCode = 'X';
        }
        if (checkCode != MOD_CHECK_CODE_ARR[sum % 11]) {
            return null;
        }

        result.setBirthday(matcher.group(2) + matcher.group(3) + matcher.group(4));
        return result;
    }

}
