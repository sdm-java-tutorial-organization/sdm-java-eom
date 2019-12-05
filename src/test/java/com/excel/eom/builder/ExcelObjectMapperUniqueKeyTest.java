package com.excel.eom.builder;

import com.excel.eom.annotation.ExcelColumn;
import com.excel.eom.annotation.ExcelObject;
import com.excel.eom.annotation.UniqueKey;
import com.excel.eom.exception.EOMBodyException;
import com.excel.eom.exception.EOMDevelopmentException;
import com.excel.eom.exception.body.EOMCellException;
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

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) // 사전식순서 (a_, b_, c_, ...)
public class ExcelObjectMapperUniqueKeyTest {

    static String DEPLOY_UNIQUE =
            "src/main/resources/deploy/uniquekey/deploy_uniquekey.xlsx";

    static String DEPLOY_UNIQUE_NOT_UNIQUE =
            "src/main/resources/deploy/uniquekey/deploy_uniquekey_notunique.xlsx";

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
            borderColor = IndexedColors.BLACK)
    @UniqueKey({"name"})
    public static class ExcelObjectUniqueKey {

        @ExcelColumn(name = "NAME", index = 0)
        public String name;

        @ExcelColumn(name = "COUNT", index = 1)
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
    public void a_buildObjectUniqueKey() throws Throwable {
        List<ExcelObjectMapperUniqueKeyTest.ExcelObjectUniqueKey> items = Arrays.asList(
                new ExcelObjectMapperUniqueKeyTest.ExcelObjectUniqueKey("A", 1),
                new ExcelObjectMapperUniqueKeyTest.ExcelObjectUniqueKey("B", 2),
                new ExcelObjectMapperUniqueKeyTest.ExcelObjectUniqueKey("C", 3),
                new ExcelObjectMapperUniqueKeyTest.ExcelObjectUniqueKey("D", 4)
        );
        Workbook book = new SXSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(book, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectMapperUniqueKeyTest.ExcelObjectUniqueKey.class)
                .initBook(book)
                .initSheet(sheet)
                .buildObject(items);
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(book, DEPLOY_UNIQUE);
    }


    @Test
    public void a_buildObjectUniqueKeyThrowNotUnique() throws Throwable {
        List<ExcelObjectMapperUniqueKeyTest.ExcelObjectUniqueKey> items = Arrays.asList(
                new ExcelObjectMapperUniqueKeyTest.ExcelObjectUniqueKey("A", 1),
                new ExcelObjectMapperUniqueKeyTest.ExcelObjectUniqueKey("A", 2),
                new ExcelObjectMapperUniqueKeyTest.ExcelObjectUniqueKey("B", 3),
                new ExcelObjectMapperUniqueKeyTest.ExcelObjectUniqueKey("B", 4)
        );
        Workbook book = new SXSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(book, "sheet");
        try {
            ExcelObjectMapper.init()
                    .initModel(ExcelObjectUniqueKey.class)
                    .initBook(book)
                    .initSheet(sheet)
                    .buildObject(items);
        } catch (EOMBodyException e) {
            EOMCellException cellException = e.getDetail().get(0);
            long nullPointerCount = e.getDetail().stream()
                    .filter(detail -> detail.getClass().getSimpleName().equals("EOMNotUniqueException"))
                    .map(detail -> {
                        System.out.println(detail.getMessage());
                        System.out.println(detail.getArgs());
                        return detail;
                    })
                    .count();
            assertEquals(nullPointerCount, 2);
        }
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(book, DEPLOY_UNIQUE_NOT_UNIQUE);
    }

}
