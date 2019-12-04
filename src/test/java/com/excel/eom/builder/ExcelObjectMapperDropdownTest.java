package com.excel.eom.builder;

import com.excel.eom.annotation.ExcelColumn;
import com.excel.eom.annotation.ExcelObject;
import com.excel.eom.annotation.UniqueKey;
import com.excel.eom.annotation.UniqueKeys;
import com.excel.eom.exception.EOMBodyException;
import com.excel.eom.exception.body.EOMCellException;
import com.excel.eom.model.Dropdown;
import com.excel.eom.tutorial.Planet;
import com.excel.eom.util.ExcelFileUtil;
import com.excel.eom.util.ExcelSheetUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) // 사전식순서 (a_, b_, c_, ...)
public class ExcelObjectMapperDropdownTest {

    static String DEPLOY_DROPDOWN =
            "src/main/resources/deploy/dropdown/deploy_dropdown.xlsx";

    static String DEPLOY_DROPDOWN_DYNAMIC =
            "src/main/resources/deploy/dropdown/deploy_dropdown_dynamic.xlsx";

    static String DEPLOY_THROW_NONCONTAIN =
            "src/main/resources/deploy/dropdown/deploy_dropdown_throw_notContain.xlsx";

    static String DEPLOY_THROW_NONCONTAIN_DYNAMIC =
            "src/main/resources/deploy/dropdown/deploy_dropdown_dynamic_throw_notContain.xlsx";

    @Data
    @NoArgsConstructor // * must have
    @AllArgsConstructor
    @ExcelObject(
            name = "EOM",
            cellColor = IndexedColors.YELLOW,
            borderColor = IndexedColors.BLACK)
    @UniqueKey({"name", "planet"})
    public static class ExcelObjectEnumDropdown {

        @ExcelColumn(name = "NAME", index=0)
        public String name;

        @ExcelColumn(name = "COUNT", index=1)
        public Integer count;

        @ExcelColumn(name = "PLANET", index=2)
        public Planet planet;

    }

    @Data
    @NoArgsConstructor // * must have
    @AllArgsConstructor
    @ExcelObject(
            name = "EOM",
            cellColor = IndexedColors.YELLOW,
            borderColor = IndexedColors.BLACK)
    @UniqueKeys({ @UniqueKey("name"), @UniqueKey("count") })
    public static class ExcelObjectDynamicDropdown {

        @ExcelColumn(name = "NAME", index=0)
        public String name;

        @ExcelColumn(name = "COUNT", index=1)
        public Integer count;

        @ExcelColumn(name = "FRUIT", index=2, dropdown="dropdownKey")
        public String fruit;

    }

    @Test
    public void a_buildObjectEnumDropdown() throws Throwable {
        List<ExcelObjectEnumDropdown> items = Arrays.asList(
                new ExcelObjectEnumDropdown("nameA", 1, Planet.Earth),
                new ExcelObjectEnumDropdown("nameB", 2, Planet.Earth),
                new ExcelObjectEnumDropdown("nameC", 3, Planet.Asgard)
        );
        Workbook bookDropdown = new SXSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(bookDropdown, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectEnumDropdown.class)
                .initBook(bookDropdown)
                .initSheet(sheet)
                .buildObject(items);
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(bookDropdown, DEPLOY_DROPDOWN);
    }

    @Test
    public void b_buildSheetEnumDropdown() throws Throwable {
        Workbook bookDropdown = ExcelFileUtil.getXSSFWorkbookByFile(new File(DEPLOY_DROPDOWN));
        Sheet sheet = ExcelSheetUtil.getSheet(bookDropdown, 0);
        List<ExcelObjectEnumDropdown> items = ExcelObjectMapper.init()
                .initModel(ExcelObjectEnumDropdown.class)
                .initBook(bookDropdown)
                .initSheet(sheet)
                .buildSheet();
        items.stream().forEach(item -> {
            System.out.println(item.toString());
        });
    }

    @Test
    public void a_buildObjectEnumDropdownThrowNotContain() throws Throwable {
        List<ExcelObjectEnumDropdown> items = Arrays.asList(
                new ExcelObjectEnumDropdown("nameA", 1, Planet.Earth),
                new ExcelObjectEnumDropdown("nameB", 2, null),
                new ExcelObjectEnumDropdown("nameC", 3, Planet.Asgard)
        );
        Workbook bookDropdownNotContain = new SXSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(bookDropdownNotContain, "sheet");
        try {
            ExcelObjectMapper.init()
                    .initModel(ExcelObjectEnumDropdown.class)
                    .initBook(bookDropdownNotContain)
                    .initSheet(sheet)
                    .buildObject(items);
        } catch (EOMBodyException e) {
            long nullPointerCount = e.getDetail().stream()
                    .filter(detail -> detail.getClass().getSimpleName().equals("EOMNotContainException"))
                    .count();
            assertEquals(nullPointerCount, 1);
        }
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(bookDropdownNotContain, DEPLOY_THROW_NONCONTAIN);
    }

    @Test
    public void b_buildSheetEnumDropdownThrowNotContain() throws Throwable {
        Workbook bookDropdownNotContain = ExcelFileUtil.getXSSFWorkbookByFile(new File(DEPLOY_THROW_NONCONTAIN));
        Sheet sheet = ExcelSheetUtil.getSheet(bookDropdownNotContain, 0);
        List<ExcelObjectEnumDropdown> items = null;
        try {
            items = ExcelObjectMapper.init()
                    .initModel(ExcelObjectEnumDropdown.class)
                    .initBook(bookDropdownNotContain)
                    .initSheet(sheet)
                    .buildSheet();
        } catch (EOMBodyException e) {
            long nullPointerCount = e.getDetail().stream()
                    .filter(detail -> detail.getClass().getSimpleName().equals("EOMNotContainException"))
                    .count();
            assertEquals(nullPointerCount, 1);
        }
        items.stream().forEach(item -> {
            System.out.println(item.toString());
        });
    }

    @Test
    public void a_buildObjectDynamicDropdown() throws Throwable {
        Map optionMap = new HashMap<>();
        optionMap.put("apple", "a");
        optionMap.put("banana", "b");
        optionMap.put("cherry", "c");
        Dropdown dropdown = new Dropdown("dropdownKey", optionMap);
        List<ExcelObjectDynamicDropdown> items = Arrays.asList(
                new ExcelObjectDynamicDropdown("nameA", 1, "a"),
                new ExcelObjectDynamicDropdown("nameB", 2, "b"),
                new ExcelObjectDynamicDropdown("nameC", 3, "c")
        );
        Workbook bookDynamicDropdown = new SXSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(bookDynamicDropdown, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectDynamicDropdown.class)
                .initBook(bookDynamicDropdown)
                .initSheet(sheet)
                .initDropDowns(dropdown)
                .buildObject(items);
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(bookDynamicDropdown, DEPLOY_DROPDOWN_DYNAMIC);
    }

    @Test
    public void b_buildSheetDynamicDropdown() throws Throwable {
        Workbook bookDynamicDropdown = ExcelFileUtil.getXSSFWorkbookByFile(new File(DEPLOY_DROPDOWN_DYNAMIC));
        Map optionMap = new HashMap<>();
        optionMap.put("apple", "a");
        optionMap.put("banana", "b");
        optionMap.put("cherry", "c");
        Dropdown dropdown = new Dropdown("dropdownKey", optionMap);
        Sheet sheet = ExcelSheetUtil.getSheet(bookDynamicDropdown, 0);
        List<ExcelObjectDynamicDropdown> items = ExcelObjectMapper.init()
                .initModel(ExcelObjectDynamicDropdown.class)
                .initBook(bookDynamicDropdown)
                .initSheet(sheet)
                .initDropDowns(dropdown)
                .buildSheet();
        items.stream().forEach(item -> {
            System.out.println(item.toString());
        });
    }

    @Test
    public void a_buildObjectDynamicDropdownThrowNotContain() throws Throwable {
        Map optionMap = new HashMap<>();
        optionMap.put("apple", "a");
        optionMap.put("banana", "b");
        optionMap.put("cherry", "c");
        Dropdown dropdown = new Dropdown("dropdownKey", optionMap);
        List<ExcelObjectDynamicDropdown> items = Arrays.asList(
                new ExcelObjectDynamicDropdown("nameA", 1, "d"),
                new ExcelObjectDynamicDropdown("nameB", 2, "e"),
                new ExcelObjectDynamicDropdown("nameC", 3, "f")
        );
        Workbook bookDynamicDropdown = new SXSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(bookDynamicDropdown, "sheet");
        try {
            ExcelObjectMapper.init()
                    .initModel(ExcelObjectDynamicDropdown.class)
                    .initBook(bookDynamicDropdown)
                    .initSheet(sheet)
                    .initDropDowns(dropdown)
                    .buildObject(items);
        } catch (EOMBodyException e) {
            long nullPointerCount = e.getDetail().stream()
                    .filter(detail -> detail.getClass().getSimpleName().equals("EOMNotContainException"))
                    .count();
            assertEquals(nullPointerCount, 3);
        }
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(bookDynamicDropdown, DEPLOY_THROW_NONCONTAIN_DYNAMIC);
    }

    @Test
    public void b_buildSheetDynamicDropdownThrowNotContain() throws Throwable {
        Map optionMap = new HashMap<>();
        optionMap.put("apple", "a");
        optionMap.put("banana", "b");
        optionMap.put("cherry", "c");
        Dropdown dropdown = new Dropdown("dropdownKey", optionMap);
        Workbook bookDynamicDropdown = ExcelFileUtil.getXSSFWorkbookByFile(new File(DEPLOY_THROW_NONCONTAIN_DYNAMIC));
        Sheet sheet = ExcelSheetUtil.getSheet(bookDynamicDropdown, 0);
        List<ExcelObjectDynamicDropdown> items = null;
        try {
            items = ExcelObjectMapper.init()
                    .initModel(ExcelObjectDynamicDropdown.class)
                    .initBook(bookDynamicDropdown)
                    .initSheet(sheet)
                    .initDropDowns(dropdown)
                    .buildSheet();
        } catch (EOMBodyException e) {
            long nullPointerCount = e.getDetail().stream()
                    .filter(detail -> detail.getClass().getSimpleName().equals("EOMNotContainException"))
                    .count();
            assertEquals(nullPointerCount, 3);
        }
        items.stream().forEach(item -> {
            System.out.println(item.toString());
        });
    }

}
