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
import java.util.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) // 사전식순서 (a_, b_, c_, ...)
public class ExcelObjectMapperDataTypeTest {

    static String DEPLOY_BASIC =
            "src/main/resources/deploy/type/deploy_basic.xlsx";

    static String DEPLOY_LONG_TYPE =
            "src/main/resources/deploy/type/deploy_long_type.xlsx";

    @Data
    @NoArgsConstructor // * must have
    @AllArgsConstructor
    @ExcelObject(
            name = "EOM",
            cellColor = IndexedColors.YELLOW,
            borderColor = IndexedColors.BLACK,
            borderStyle = CellStyle.BORDER_THIN)
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
            borderStyle = CellStyle.BORDER_THIN)
    @UniqueKey("name")
    public static class ExcelObjectLong {

        @ExcelColumn(name = "NAME", index=0, nullable = false)
        public String name;

        @ExcelColumn(name = "COUNT", index=1)
        public Long count;

        @ExcelColumn(name = "COUNT", index=2)
        public long count2;

    }

    // ==============================================================
    // Object -> Sheet
    // ==============================================================

    @Test
    public void a_buildObject() throws Throwable {
        List<ExcelObjectBasic> items = Arrays.asList(
                new ExcelObjectBasic("nameA", 1),
                new ExcelObjectBasic("nameB", 2),
                new ExcelObjectBasic("nameC", 3)
        );
        Workbook book = new SXSSFWorkbook();
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
    public void a_buildObject_long() throws Throwable {
        List<ExcelObjectLong> items = Arrays.asList(
                new ExcelObjectLong("nameA", (long) 1, (long) 2),
                new ExcelObjectLong("nameB", (long) 1, (long) 2),
                new ExcelObjectLong("nameC", (long) 1, (long) 2)
        );
        Workbook book = new SXSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(book, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectLong.class)
                .initBook(book)
                .initSheet(sheet)
                .buildObject(items);
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(book, DEPLOY_LONG_TYPE);
    }

    // ==============================================================
    // Sheet -> Object
    // ==============================================================

    @Test
    public void b_buildSheet_long() throws Throwable {
        Workbook book = ExcelFileUtil.getXSSFWorkbookByFile(new File(DEPLOY_LONG_TYPE));
        Sheet sheet = ExcelSheetUtil.getSheet(book, 0);
        ExcelSheetUtil.print(sheet);
        List<ExcelObjectLong> items = ExcelObjectMapper.init()
                .initModel(ExcelObjectLong.class)
                .initBook(book)
                .initSheet(sheet)
                .buildSheet();
        items.stream().forEach(item -> {
            System.out.println(item.toString());
        });
    }

    @Test
    public void b_buildSheet_nullable() throws Throwable {
        Workbook book = ExcelFileUtil.getXSSFWorkbookByFile(new File(DEPLOY_BASIC));
        Sheet sheet = ExcelSheetUtil.getSheet(book, 0);
        ExcelSheetUtil.print(sheet);
        List<ExcelObjectBasic> items = ExcelObjectMapper.init()
                .initModel(ExcelObjectBasic.class)
                .initBook(book)
                .initSheet(sheet)
                .buildSheet();
        items.stream().forEach(item -> {
            System.out.println(item.toString());
        });
    }


}
