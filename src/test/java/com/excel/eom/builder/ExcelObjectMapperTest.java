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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) // 사전식순서 (a_, b_, c_, ...)
public class ExcelObjectMapperTest {

    static String DEPLOY_BASIC =
            "src/main/resources/deploy/basic/deploy_basic.xlsx";

    static String DEPLOY_NULLABLE =
            "src/main/resources/deploy/basic/deploy_nullable.xlsx";

    static String DEPLOY_NOT_UNIQUE =
            "src/main/resources/deploy/basic/deploy_notunique.xlsx";

    static String DEPLOY_SXSSF =
            "src/main/resources/deploy/deploy_sxssf.xlsx";

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
                .initModel(ExcelObjectBasic.class).initBook(book).initSheet(sheet)
                .buildObject(items);
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(book, DEPLOY_BASIC);
    }

    @Test
    public void b_buildSheet() throws Throwable {
        Workbook book = ExcelFileUtil.getXSSFWorkbookByFile(new File(DEPLOY_BASIC));
        Sheet sheet = ExcelSheetUtil.getSheet(book, 0);
        ExcelSheetUtil.print(sheet);
        List<ExcelObjectBasic> items = ExcelObjectMapper.init()
                .initModel(ExcelObjectBasic.class).initBook(book).initSheet(sheet)
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
                // null A
                new ExcelObjectBasic("", 3),
                // null B
                new ExcelObjectBasic(null, 4),
                new ExcelObjectBasic("nameE", 5)
        );
        Workbook bookNullable = new SXSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(bookNullable, "sheet");
        try {
            ExcelObjectMapper.init()
                    .initModel(ExcelObjectBasic.class).initBook(bookNullable).initSheet(sheet)
                    .buildObject(items);
        } catch (EOMBodyException e) {
            EOMCellException cellException = e.getDetail().get(0);
            long nullPointerCount = e.getDetail().stream()
                    .filter(detail -> detail.getClass().getSimpleName().equals("EOMNotNullException"))
                    .count();
            assertEquals(nullPointerCount, 2);
        }
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(bookNullable, DEPLOY_NULLABLE);
    }

    @Test
    public void b_buildSheetThrowNullable() throws Throwable {
        Workbook book = ExcelFileUtil.getXSSFWorkbookByFile(new File(DEPLOY_NULLABLE));
        Sheet sheet = ExcelSheetUtil.getSheet(book, 0);
        List<ExcelObjectBasic> items = null;
        try {
            items = ExcelObjectMapper.init()
                    .initModel(ExcelObjectBasic.class).initBook(book).initSheet(sheet)
                    .buildSheet();
            items.stream().forEach(item -> {
                System.out.println(item.toString());
            });
        } catch (EOMBodyException e) {
            long nullPointerCount = e.getDetail().stream()
                    .filter(detail -> detail.getClass().getSimpleName().equals("EOMNotNullException"))
                    .count();
            assertEquals(nullPointerCount, 2);
        }
    }

    @Test
    public void a_buildObjectThrowNotUnique() throws Throwable {
        List<ExcelObjectBasic> items = Arrays.asList(
                new ExcelObjectBasic("nameA", 1),
                new ExcelObjectBasic("nameA", 2)
        );
        Workbook bookNotUnique = new SXSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(bookNotUnique, "sheet");
        try {
            ExcelObjectMapper.init()
                    .initModel(ExcelObjectBasic.class).initBook(bookNotUnique).initSheet(sheet)
                    .buildObject(items);
        } catch (EOMBodyException e) {
            /*e.printStackTrace();*/
            long nullPointerCount = e.getDetail().stream()
                    .filter(detail -> detail.getClass().getSimpleName().equals("EOMNotUniqueException"))
                    .count();
            assertEquals(nullPointerCount, 1);
        }
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(bookNotUnique, DEPLOY_NOT_UNIQUE);
    }

    @Test
    public void b_buildSheetThrowNotUnique() throws Throwable {
        Workbook book = ExcelFileUtil.getXSSFWorkbookByFile(new File(DEPLOY_NOT_UNIQUE));
        Sheet sheet = ExcelSheetUtil.getSheet(book, 0);
        List<ExcelObjectBasic> items = null;
        try {
            items = ExcelObjectMapper.init()
                    .initModel(ExcelObjectBasic.class).initBook(book).initSheet(sheet)
                    .buildSheet();
            items.stream().forEach(item -> {
                System.out.println(item.toString());
            });
        } catch (EOMBodyException e) {
            long nullPointerCount = e.getDetail().stream()
                    .filter(detail -> detail.getClass().getSimpleName().equals("EOMNotUniqueException"))
                    .count();
            assertEquals(nullPointerCount, 1);
        }
    }

    @Test
    public void a_buildObjectWithPerfermanceByXSSF() throws Throwable {
        List<ExcelObjectBasic> items = new ArrayList<>();
        for(int i=0; i<500000; i++) {
            items.add(new ExcelObjectBasic("name" + i, i));
        }
        long beforeTime = System.nanoTime();
        Workbook book = new SXSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(book, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectBasic.class).initBook(book).initSheet(sheet)
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
        Workbook book = new SXSSFWorkbook();
        Sheet sheet = ExcelSheetUtil.initSheet(book, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectBasic.class).initBook(book).initSheet(sheet)
                .buildObject(items);
        long afterTime = System.nanoTime();
        System.out.println(afterTime - beforeTime);
        ExcelSheetUtil.print(sheet);
        ExcelFileUtil.writeExcel(book, DEPLOY_SXSSF);
    }

}
