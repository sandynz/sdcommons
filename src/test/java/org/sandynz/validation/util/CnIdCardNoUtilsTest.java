package org.sandynz.validation.util;

import org.junit.Assert;
import org.junit.Test;

public class CnIdCardNoUtilsTest {

    @Test
    public void test() {
        String cnIdCardNo = "53010219200508011x";
        CnIdCardNoBean bean = CnIdCardNoUtils.parseCnIdCardNo(cnIdCardNo);
        System.out.println("bean=" + bean);
        Assert.assertEquals("19200508", bean.getBirthday());
        Assert.assertEquals("530102", bean.getAreaCode());
        Assert.assertEquals(1920, bean.getYear());
        Assert.assertEquals(5, bean.getMonth());
        Assert.assertEquals(8, bean.getDay());
        Assert.assertEquals("011", bean.getSeqCode());
        Assert.assertEquals("x", bean.getCheckCode());
    }

}
