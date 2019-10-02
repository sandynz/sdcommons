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
package org.sandynz.sdcommons.base.time;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Joda DateTimeFormatter collection.
 *
 * @author sandynz
 */
public class JodaDateTimeFormatters {

    /**
     * e.g. 2019-01-01T14:30:05
     */
    public static final DateTimeFormatter yyyyMMddTHHmmss = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * e.g. 2019-01-01 14:30:05.819+0800
     */
    public static final DateTimeFormatter yyyyMMddHHmmssSSSZ = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSZ");

    /**
     * e.g. 2019-01-01 14:30:05
     */
    public static final DateTimeFormatter yyyyMMddHHmmss = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * e.g. 2019-01-01 14:30
     */
    public static final DateTimeFormatter yyyyMMddHHmm = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

    /**
     * e.g. 2019-01-01 14
     */
    public static final DateTimeFormatter yyyyMMddHH = DateTimeFormat.forPattern("yyyy-MM-dd HH");

    /**
     * e.g. 2019-01-01
     */
    public static final DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyy-MM-dd");

    /**
     * e.g. 2019-01
     */
    public static final DateTimeFormatter yyyyMM = DateTimeFormat.forPattern("yyyy-MM");

    /**
     * e.g. 2019
     */
    public static final DateTimeFormatter yyyy = DateTimeFormat.forPattern("yyyy");

    /**
     * e.g. 01-01 14:30
     */
    public static final DateTimeFormatter MMddHHmm = DateTimeFormat.forPattern("MM-dd HH:mm");

    /**
     * e.g. 14:30:05
     */
    public static final DateTimeFormatter HHmmss = DateTimeFormat.forPattern("HH:mm:ss");

    /**
     * e.g. 14:30
     */
    public static final DateTimeFormatter HHmm = DateTimeFormat.forPattern("HH:mm");

    /**
     * e.g. 20190101
     */
    public static final DateTimeFormatter yyyyMMddNoDelimiter = DateTimeFormat.forPattern("yyyyMMdd");

    /**
     * e.g. 20190101143005
     */
    public static final DateTimeFormatter yyyyMMddHHmmssNoDelimiter = DateTimeFormat.forPattern("yyyyMMddHHmmss");

}
