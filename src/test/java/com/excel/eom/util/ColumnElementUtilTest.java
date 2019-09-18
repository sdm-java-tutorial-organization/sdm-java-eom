package com.excel.eom.util;

import com.excel.eom.annotation.ExcelColumn;
import com.excel.eom.model.ColumnElement;
import com.excel.eom.tutorial.ExcelObjectDemo;
import com.excel.eom.tutorial.Planet;
import com.excel.eom.util.callback.ExcelColumnInfoCallback;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ColumnElementUtilTest {

    public class ColumnElementTest {

        @ExcelColumn(name = "PLANET2", group = 1, index = 1)
        public Planet plane2;

        @ExcelColumn(name = "NAME", group = 0, index = 2)
        public String name;

        @ExcelColumn(name = "HEIGHT", group = 0, index = 3)
        public Integer height;

        @ExcelColumn(name = "WEIGHT", group = 0, index = 4)
        public Integer weight;

        @ExcelColumn(name = "PLANET", group = 0, index = 5)
        public Planet planet;

        @ExcelColumn(name = "GROUP", group = 1, index = 0)
        public String group;

    }

    @Test
    public void getElement() {
        Map<Field, ColumnElement> elements = new HashMap<>();
        ReflectionUtil.getFieldInfo(ColumnElementTest.class, new ExcelColumnInfoCallback() {
            @Override
            public void getFieldInfo(Field field,
                                     String name,
                                     Integer index,
                                     Integer group,
                                     IndexedColors cellColor,
                                     BorderStyle borderStyle,
                                     IndexedColors borderColor) {
                elements.put(field, new ColumnElement(name, index, group));
            }
        });
    }

    @Test
    public void getRegionElement() {
        Map<Field, ColumnElement> elements = new HashMap<>();
        ReflectionUtil.getFieldInfo(ColumnElementTest.class, new ExcelColumnInfoCallback() {
            @Override
            public void getFieldInfo(Field field,
                                     String name,
                                     Integer index,
                                     Integer group,
                                     IndexedColors cellColor,
                                     BorderStyle borderStyle,
                                     IndexedColors borderColor) {
                elements.put(field, new ColumnElement(name, index, group));
            }
        });
        Map<Field, ColumnElement> regionElement = ColumnElementUtil.getRegionElement(elements);
        System.out.println(regionElement);
    }
}