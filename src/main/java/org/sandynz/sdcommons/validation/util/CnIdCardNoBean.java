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

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 中国居民身份证号码
 */
@Data
@Accessors(chain = true)
@ToString
public class CnIdCardNoBean {

    /**
     * 六位地址码
     */
    private String areaCode;

    /**
     * 八位出生日期码
     */
    private String birthday;

    private int year;

    private int month;

    private int day;

    /**
     * 三位数字顺序码
     */
    private String seqCode;

    /**
     * 一位校验码
     */
    private String checkCode;

}
