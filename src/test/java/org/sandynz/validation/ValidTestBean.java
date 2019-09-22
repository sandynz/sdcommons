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
package org.sandynz.validation;

import lombok.Data;
import lombok.experimental.Accessors;
import org.sandynz.validation.constraints.CnIdCardNo;
import org.sandynz.validation.constraints.EqualsAnyInt;
import org.sandynz.validation.constraints.EqualsAnyString;
import org.sandynz.validation.constraints.LooseCnMobile;
import org.sandynz.validation.constraints.LooseCnTel;
import org.sandynz.validation.constraints.hibernate.LooseCnMobileOrTel;

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
    private Integer type;

    @EqualsAnyString(value = {"Male", "Female"}, message = "Must be one of Male / Female")
    private String sex;

    @CnIdCardNo
    private String cnIdCardNo1;

    @CnIdCardNo(minAge = 18, maxAge = 65)
    private String cnIdCardNo2;

}
