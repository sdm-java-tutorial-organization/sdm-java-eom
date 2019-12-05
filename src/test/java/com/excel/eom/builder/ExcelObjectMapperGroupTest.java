package com.excel.eom.builder;

import com.excel.eom.annotation.ExcelColumn;
import com.excel.eom.annotation.ExcelObject;
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
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) // 사전식순서 (a_, b_, c_, ...)
public class ExcelObjectMapperGroupTest {

    static String DEPLOY_GROUP_2 =
            "src/main/resources/deploy/group/deploy_group_2.xlsx";

    static String DEPLOY_GROUP_2_THROW =
            "src/main/resources/deploy/group/deploy_group_2_throw.xlsx";

    static String DEPLOY_GROUP_3 =
        "src/main/resources/deploy/deploy_group_3.xlsx";

    @Data
    @NoArgsConstructor // * must have
    @AllArgsConstructor
    @ExcelObject(
            name = "EOM",
            cellColor = IndexedColors.YELLOW,
            borderColor = IndexedColors.BLACK,
            borderStyle = CellStyle.BORDER_THIN)
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
            borderStyle = CellStyle.BORDER_THIN)
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
        Workbook bookGroupA = new SXSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(bookGroupA, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectTwoDepth.class)
                .initBook(bookGroupA)
                .initSheet(sheet)
                .buildObject(items);
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(bookGroupA, DEPLOY_GROUP_2);
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
        Workbook bookGroupA = new SXSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(bookGroupA, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectTwoDepth.class)
                .initBook(bookGroupA)
                .initSheet(sheet)
                .buildObject(items);
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(bookGroupA, DEPLOY_GROUP_2_THROW);
    }

    @Test
    public void b_buildSheetWithTwoDepth() throws Throwable {
        Workbook bookGroupA = ExcelFileUtil.getXSSFWorkbookByFile(new File(DEPLOY_GROUP_2_THROW));
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

        Workbook book = new SXSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(book, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectThreeDepth.class)
                .initBook(book)
                .initSheet(sheet)
                .buildObject(items);
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(book, DEPLOY_GROUP_3);
    }

    @Test
    public void b_buildSheetWithThreeDepth() throws Throwable {
        Workbook bookGroupB = ExcelFileUtil.getXSSFWorkbookByFile(new File(DEPLOY_GROUP_3));
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

}
