package com.excel.eom.util;

import com.excel.eom.model.deprecated.HeaderElement;
import com.excel.eom.tutorial.deprecated.MultipleParentColumnOld;
import com.excel.eom.util.deprecated.HeaderElementUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class HeaderElementUtilTest {

    @Test
    public void parseColumnToElement() {
        List<HeaderElement> elements = new ArrayList<>();
        HeaderElementUtil.parseColumnToElement(elements, MultipleParentColumnOld.values(), HeaderElement.DEFAULT_LEVEL);
        assertEquals(elements.size(), 4);
        System.out.println(
                Arrays.toString(elements.stream()
                        .map(element -> element.getName())
                        .toArray()));
    }
}