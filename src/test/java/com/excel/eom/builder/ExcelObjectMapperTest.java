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
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) // 사전식순서 (a_, b_, c_, ...)
public class ExcelObjectMapperTest {

    static Workbook book;
    static Workbook bookNullable;
    static Workbook bookDropdown;
    static Workbook bookDropdownNotContain;
    static Workbook bookDynamicDropdown;
    static Workbook bookGroupA;
    static Workbook bookGroupB;

    static String DEPLOY_BASIC = "src/main/resources/deploy/deploy_basic.xlsx";

    @Test
    public void a_buildObject() throws Throwable {
        List<ExcelObjectBasic> items = Arrays.asList(
                new ExcelObjectBasic("nameA", 1),
                new ExcelObjectBasic("nameB", 2),
                new ExcelObjectBasic("nameC", 3)
        );
        book = new XSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(book, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectBasic.class)
                .initBook(book)
                .initSheet(sheet)
                .buildObject(items);
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(book, DEPLOY_BASIC);
    }

    @Test
    public void b_buildSheet() throws Throwable {
        Sheet sheet = ExcelSheetUtil.getSheet(book, 0);
        List<ExcelObjectBasic> items = ExcelObjectMapper.init()
                .initModel(ExcelObjectBasic.class)
                .initBook(book)
                .initSheet(sheet)
                .buildSheet();
        items.stream().forEach(item -> {
            System.out.println(item.toString());
        });
    }

    @Test
    public void a_buildObjectThrowNullable() throws Throwable {
        List<ExcelObjectBasic> items = Arrays.asList(
                new ExcelObjectBasic("nameA", 1),
                new ExcelObjectBasic("nameB", 2),
                new ExcelObjectBasic("", 3),
                new ExcelObjectBasic(null, 4),
                new ExcelObjectBasic("nameE", 5)
        );
        bookNullable = new XSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(bookNullable, "sheet");
        try {
            ExcelObjectMapper.init()
                    .initModel(ExcelObjectBasic.class)
                    .initBook(bookNullable)
                    .initSheet(sheet)
                    .buildObject(items);
        } catch (EOMBodyException e) {
            e.printStackTrace();
        }
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(bookNullable, "src/main/resources/deploy/deploy_nullable.xlsx");
    }

    @Test
    public void b_buildSheetThrowNullable() throws Throwable {
        Sheet sheet = ExcelSheetUtil.getSheet(bookNullable, 0);
        List<ExcelObjectBasic> items = null;
        try {
            items = ExcelObjectMapper.init()
                    .initModel(ExcelObjectBasic.class)
                    .initBook(bookNullable)
                    .initSheet(sheet)
                    .buildSheet();

            items.stream().forEach(item -> {
                System.out.println(item.toString());
            });
        } catch (EOMBodyException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void a_buildObjectWithDropdown() throws Throwable {
        List<ExcelObjectDropdown> items = Arrays.asList(
                new ExcelObjectDropdown("nameA", 1, Planet.Earth),
                new ExcelObjectDropdown("nameB", 2, Planet.Earth),
                new ExcelObjectDropdown("nameC", 3, Planet.Asgard)
        );
        bookDropdown = new XSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(bookDropdown, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectDropdown.class)
                .initBook(bookDropdown)
                .initSheet(sheet)
                .buildObject(items);
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(bookDropdown, "src/main/resources/deploy/deploy_dropdown.xlsx");
    }

    @Test
    public void b_buildSheetWithDropdown() throws Throwable {
        Workbook bookDropdown = ExcelFileUtil.getXSSFWorkbookByFile(
                new File("src/main/resources/deploy/deploy_dropdown.xlsx"));
        Sheet sheet = ExcelSheetUtil.getSheet(bookDropdown, 0);
        List<ExcelObjectDropdown> items = ExcelObjectMapper.init()
                .initModel(ExcelObjectDropdown.class)
                .initBook(bookDropdown)
                .initSheet(sheet)
                .buildSheet();
        items.stream().forEach(item -> {
            System.out.println(item.toString());
        });
    }

    @Test
    public void a_buildObjectWithDropdownThrowNotContain() throws Throwable {
        List<ExcelObjectDropdown> items = Arrays.asList(
                new ExcelObjectDropdown("nameA", 1, Planet.Earth),
                new ExcelObjectDropdown("nameB", 2, null),
                new ExcelObjectDropdown("nameC", 3, Planet.Asgard)
        );

        bookDropdownNotContain = new XSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(bookDropdownNotContain, "sheet");
        try {
            ExcelObjectMapper.init()
                    .initModel(ExcelObjectDropdown.class)
                    .initBook(bookDropdownNotContain)
                    .initSheet(sheet)
                    .buildObject(items);
        } catch (EOMBodyException e) {
            for(EOMCellException detail : e.getDetail()) {
                detail.printStackTrace();
            }
        }
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(bookDropdownNotContain, "src/main/resources/deploy/deploy_dropdown_throw_notContain.xlsx");
    }

    @Test
    public void b_buildSheetWithDropdownThrowNotContain() throws Throwable {
        Workbook bookDropdownNotContain = ExcelFileUtil.getXSSFWorkbookByFile(
                new File("src/main/resources/deploy/deploy_dropdown_throw_notContain.xlsx"));
        Sheet sheet = ExcelSheetUtil.getSheet(bookDropdownNotContain, 0);
        List<ExcelObjectDropdown> items = null;
        try {
            items = ExcelObjectMapper.init()
                    .initModel(ExcelObjectDropdown.class)
                    .initBook(bookDropdownNotContain)
                    .initSheet(sheet)
                    .buildSheet();
        } catch (EOMBodyException e) {
            for(EOMCellException detail : e.getDetail()) {
                detail.printStackTrace();
            }
        }
        items.stream().forEach(item -> {
            System.out.println(item.toString());
        });
    }

    @Test
    public void a_buildObjectWithDynamicDropdown() throws Throwable {
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
        bookDynamicDropdown = new XSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(bookDynamicDropdown, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectDynamicDropdown.class)
                .initBook(bookDynamicDropdown)
                .initSheet(sheet)
                .initDropDowns(dropdown)
                .buildObject(items);
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(bookDynamicDropdown, "src/main/resources/deploy/deploy_dropdown_dynamic.xlsx");
    }

    @Test
    public void b_buildSheetWithDynamicDropdown() throws Throwable {
        Workbook bookDynamicDropdown = ExcelFileUtil.getXSSFWorkbookByFile(
                new File("src/main/resources/deploy/deploy_dropdown_dynamic.xlsx"));
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
        ExcelSheetUtil.print(sheet);
    }

    @Test
    public void a_buildObjectWithDynamicDropdownThrowNotContain() throws Throwable {
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
        bookDynamicDropdown = new XSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(bookDynamicDropdown, "sheet");
        try {
            ExcelObjectMapper.init()
                    .initModel(ExcelObjectDynamicDropdown.class)
                    .initBook(bookDynamicDropdown)
                    .initSheet(sheet)
                    .initDropDowns(dropdown)
                    .buildObject(items);
        } catch (EOMBodyException e) {
            for(EOMCellException detail : e.getDetail()) {
                detail.printStackTrace();
            }
        }
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(bookDynamicDropdown, "src/main/resources/deploy/deploy_dropdown_dynamic_throw_notContain.xlsx");
    }

    @Test
    public void b_buildSheetWithDynamicDropdownThrowNotContain() throws Throwable {
        Map optionMap = new HashMap<>();
        optionMap.put("apple", "a");
        optionMap.put("banana", "b");
        optionMap.put("cherry", "c");
        Dropdown dropdown = new Dropdown("dropdownKey", optionMap);
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
            for(EOMCellException detail : e.getDetail()) {
                detail.printStackTrace();
            }
        }
        items.stream().forEach(item -> {
            System.out.println(item.toString());
        });
        ExcelSheetUtil.print(sheet);
    }

    @Test
    public void a_buildObjectWithTwoDepth() throws Throwable {
        List<ExcelObjectTwoDepth> items = Arrays.asList(
                new ExcelObjectTwoDepth("A", "catNameA", "nameA", 1),
                new ExcelObjectTwoDepth("A", "catNameA", "nameB", 2),
                new ExcelObjectTwoDepth("A", "catNameA", "nameC", 3),
                new ExcelObjectTwoDepth("A", "catNameA", "nameD", 4),
                new ExcelObjectTwoDepth("B", "catNameA", "nameE", 5),
                new ExcelObjectTwoDepth("B", "catNameA", "nameF", 6),
                new ExcelObjectTwoDepth("B", "catNameA", "nameG", 7),
                new ExcelObjectTwoDepth("B", "catNameA", "nameH", 8),
                new ExcelObjectTwoDepth("C", "catNameA", "nameI", 9),
                new ExcelObjectTwoDepth("C", "catNameA", "nameJ", 10),
                new ExcelObjectTwoDepth("C", "catNameA", "nameK", 11)
        );
        bookGroupA = new XSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(bookGroupA, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectTwoDepth.class)
                .initBook(bookGroupA)
                .initSheet(sheet)
                .buildObject(items);
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(bookGroupA, "src/main/resources/deploy/deploy_group_2.xlsx");
    }

    @Test
    public void a_buildObjectWithTwoDepthThrow() throws Throwable {
        List<ExcelObjectTwoDepth> items = Arrays.asList(
                new ExcelObjectTwoDepth("A", "catNameA", "nameA", 1),
                new ExcelObjectTwoDepth("A", "catNameA", "nameB", 2),
                new ExcelObjectTwoDepth("A", "catNameB", "nameC", 3),
                new ExcelObjectTwoDepth("A", "catNameB", "nameD", 4),
                new ExcelObjectTwoDepth("B", "catNameA", "nameE", 5),
                new ExcelObjectTwoDepth("B", "catNameA", "nameF", 6),
                new ExcelObjectTwoDepth("B", "catNameA", "nameG", 7),
                new ExcelObjectTwoDepth("B", "catNameA", "nameH", 8),
                new ExcelObjectTwoDepth("C", "catNameA", "nameI", 9),
                new ExcelObjectTwoDepth("C", "catNameA", "nameJ", 10),
                new ExcelObjectTwoDepth("C", "catNameA", "nameK", 11)
        );
        bookGroupA = new XSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(bookGroupA, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectTwoDepth.class)
                .initBook(bookGroupA)
                .initSheet(sheet)
                .buildObject(items);
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(bookGroupA, "src/main/resources/deploy/deploy_group_2_throw.xlsx");
    }

    @Test
    public void b_buildSheetWithTwoDepth() throws Throwable {
        Workbook bookGroupA = ExcelFileUtil.getXSSFWorkbookByFile(
                new File("src/main/resources/deploy/deploy_group_2.xlsx"));
        Sheet sheet = ExcelSheetUtil.getSheet(bookGroupA, 0);
        List<ExcelObjectTwoDepth> items = ExcelObjectMapper.init()
                .initModel(ExcelObjectTwoDepth.class)
                .initBook(bookGroupA)
                .initSheet(sheet)
                .buildSheet();
        items.stream().forEach(item -> {
            System.out.println(item.toString());
        });
        ExcelSheetUtil.print(sheet);
    }

    @Test
    public void a_buildObjectWithThreeDepth() throws Throwable {
        List<ExcelObjectThreeDepth> items = Arrays.asList(
                new ExcelObjectThreeDepth("A", "GroupA", "A`", "nameA", 1),
                new ExcelObjectThreeDepth("A", "GroupA", "A`","nameB", 2),
                new ExcelObjectThreeDepth("A", "GroupA", "B`","nameC", 3),
                new ExcelObjectThreeDepth("A", "GroupA", "B`","nameD", 4),
                new ExcelObjectThreeDepth("B", "GroupA", "A`","nameE", 5),
                new ExcelObjectThreeDepth("B", "GroupA", "A`","nameF", 6),
                new ExcelObjectThreeDepth("B", "GroupA", "B`","nameG", 7),
                new ExcelObjectThreeDepth("B", "GroupA", "B`","nameH", 8),
                new ExcelObjectThreeDepth("C", "GroupA", "B`","nameI", 9),
                new ExcelObjectThreeDepth("C", "GroupA", "B`","nameJ", 10),
                new ExcelObjectThreeDepth("C", "GroupA", "B`","nameK", 11),
                new ExcelObjectThreeDepth("C", "GroupA", "B`","nameL", 12)
        );

        XSSFWorkbook book = new XSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(book, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectThreeDepth.class)
                .initBook(book)
                .initSheet(sheet)
                .buildObject(items);
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(book, "src/main/resources/deploy/deploy_group_3.xlsx");
    }

    @Test
    public void b_buildSheetWithThreeDepth() throws Throwable {
        Workbook bookGroupB = ExcelFileUtil.getXSSFWorkbookByFile(
                new File("src/main/resources/deploy/deploy_group_3.xlsx"));
        Sheet sheet = ExcelSheetUtil.getSheet(bookGroupB, 0);
        List<ExcelObjectThreeDepth> items = ExcelObjectMapper.init()
                .initModel(ExcelObjectThreeDepth.class)
                .initBook(bookGroupB)
                .initSheet(sheet)
                .buildSheet();
        items.stream().forEach(item -> {
            System.out.println(item.toString());
        });
        ExcelSheetUtil.print(sheet);
    }

    @Test
    public void a_buildObjectWithPerfermanceByXSSF() throws Throwable {
        List<ExcelObjectBasic> items = new ArrayList<>();
        for(int i=0; i<500000; i++) {
            items.add(new ExcelObjectBasic("name" + i, i));
        }
        long beforeTime = System.nanoTime();
        book = new XSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(book, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectBasic.class)
                .initBook(book)
                .initSheet(sheet)
                .buildObject(items);
        long afterTime = System.nanoTime();
        System.out.println(afterTime - beforeTime);
    }

    @Test
    public void a_buildObjectWithPerfermanceBySXSSF() throws Throwable {
        List<ExcelObjectBasic> items = new ArrayList<>();
        for(int i=0; i<500000; i++) {
            items.add(new ExcelObjectBasic("name" + i, i));
        }
        long beforeTime = System.nanoTime();
        book = new SXSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(book, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectBasic.class)
                .initBook(book)
                .initSheet(sheet)
                .buildObject(items);
        long afterTime = System.nanoTime();
        System.out.println(afterTime - beforeTime);
    }

    @Data
    @NoArgsConstructor // * must have
    @AllArgsConstructor
    @ExcelObject(
            name = "EOM",
            cellColor = IndexedColors.YELLOW,
            borderColor = IndexedColors.BLACK,
            borderStyle = BorderStyle.THIN)
    @UniqueKey("name")
    public static class ExcelObjectBasic {

        @ExcelColumn(name = "NAME", index=0, nullable = false)
        public String name;

        @ExcelColumn(name = "COUNT", index=1)
        public Integer count;

    }

    @Data
    @NoArgsConstructor // * must have
    @AllArgsConstructor
    @ExcelObject(
            name = "EOM",
            cellColor = IndexedColors.YELLOW,
            borderColor = IndexedColors.BLACK,
            borderStyle = BorderStyle.THIN)
    @UniqueKey({"name", "planet"})
    public static class ExcelObjectDropdown {

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
            borderColor = IndexedColors.BLACK,
            borderStyle = BorderStyle.THIN)
    @UniqueKeys({
            @UniqueKey("name"),
            @UniqueKey("count")
    })
    public static class ExcelObjectDynamicDropdown {

        @ExcelColumn(name = "NAME", index=0)
        public String name;

        @ExcelColumn(name = "COUNT", index=1)
        public Integer count;

        @ExcelColumn(name = "FRUIT", index=2, dropdown="dropdownKey")
        public String fruit;

    }

    @Data
    @NoArgsConstructor // * must have
    @AllArgsConstructor
    @ExcelObject(
            name = "EOM",
            cellColor = IndexedColors.YELLOW,
            borderColor = IndexedColors.BLACK,
            borderStyle = BorderStyle.THIN)
    public static class ExcelObjectTwoDepth {

        @ExcelColumn(name = "CAT", group = 1, index = 0)
        public String cat;

        @ExcelColumn(name = "CAT_NAME", group = 1, index = 1)
        public String catName;

        @ExcelColumn(name = "NAME", index = 2)
        public String name;

        @ExcelColumn(name = "COUNT", index = 3)
        public Integer count;

    }

    @Data
    @NoArgsConstructor // * must have
    @AllArgsConstructor
    @ExcelObject(
            name = "EOM",
            cellColor = IndexedColors.YELLOW,
            borderColor = IndexedColors.BLACK,
            borderStyle = BorderStyle.THIN)
    public static class ExcelObjectThreeDepth {

        @ExcelColumn(name = "PARENT", group = 2, index = 0, cellColor = IndexedColors.GREY_50_PERCENT)
        public String parentGroup;

        @ExcelColumn(name = "PARENT_NAME", group = 2, index = 1, cellColor = IndexedColors.GREY_50_PERCENT)
        public String parentGroupName;

        @ExcelColumn(name = "CHILD", group = 1, index = 2, cellColor = IndexedColors.GREY_25_PERCENT)
        public String childGroup;

        @ExcelColumn(name = "NAME", index = 3)
        public String name;

        @ExcelColumn(name = "COUNT", index = 4)
        public Integer count;

    }

}
