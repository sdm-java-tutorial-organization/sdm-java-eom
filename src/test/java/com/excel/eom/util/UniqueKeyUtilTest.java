package com.excel.eom.util;

import com.excel.eom.annotation.UniqueKey;
import com.excel.eom.annotation.UniqueKeys;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UniqueKeyUtilTest {

    @UniqueKey("name")
    static class UniqueKeySingle {

        String name;
    }

    @UniqueKey({"name", "count"})
    static class UniqueKeyComposite {

        String name;

        Integer count;
    }

    @UniqueKeys({
        @UniqueKey({"name", "count"}),
        @UniqueKey({"name", "count", "type"})
    })
    static class UniqueKeyMultiple {

        String name;

        Integer count;


    }

    @UniqueKey()
    static class UniqueKeySingleNull {

    }

    @Test
    public void getUniqueValues() {
        List<List<String>> resultA = UniqueKeyUtil.getUniqueValues(UniqueKeySingle.class);
        assertEquals(resultA.size(), 1);
        assertEquals(resultA.get(0).size(), 1);

        List<List<String>> resultB = UniqueKeyUtil.getUniqueValues(UniqueKeyComposite.class);
        assertEquals(resultB.size(), 1);
        assertEquals(resultB.get(0).size(), 2);

        List<List<String>> resultC = UniqueKeyUtil.getUniqueValues(UniqueKeyMultiple.class);
        assertEquals(resultC.size(), 2);
        assertEquals(resultC.get(0).size(), 2);
        assertEquals(resultC.get(1).size(), 3);

        List<List<String>> resultD = UniqueKeyUtil.getUniqueValues(UniqueKeySingleNull.class);
        assertEquals(resultD.size(), 0);
    }

    @Test
    public void validateField() {
        List<List<String>> resultA = UniqueKeyUtil.getUniqueValues(UniqueKeySingle.class);
        boolean validationA = UniqueKeyUtil.validateField(UniqueKeySingle.class.getDeclaredFields(), resultA);
        System.out.println(validationA);
        assertTrue(validationA);

        List<List<String>> resultB = UniqueKeyUtil.getUniqueValues(UniqueKeyComposite.class);
        boolean validationB = UniqueKeyUtil.validateField(UniqueKeyComposite.class.getDeclaredFields(), resultB);
        System.out.println(validationB);
        assertTrue(validationB);

        List<List<String>> resultC = UniqueKeyUtil.getUniqueValues(UniqueKeyMultiple.class);
        boolean validationC = UniqueKeyUtil.validateField(UniqueKeyMultiple.class.getDeclaredFields(), resultC);
        System.out.println(validationC);
        assertFalse(validationC);
    }


}
