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

    static String DEPLOY_BASIC = "src/main/resources/deploy/deploy_basic.xlsx";
    static String DEPLOY_NULLABLE = "src/main/resources/deploy/deploy_nullable.xlsx";
    static String DEPLOY_DROPDOWN = "src/main/resources/deploy/deploy_dropdown.xlsx";
    static String DEPLOY_DROPDOWN_DYNAMIC = "src/main/resources/deploy/deploy_dropdown_dynamic.xlsx";
    static String DEPLOY_THROW_NONCONTAIN = "src/main/resources/deploy/deploy_dropdown_throw_notContain.xlsx";
    static String DEPLOY_THROW_NONCONTAIN_DYNAMIC = "src/main/resources/deploy/deploy_dropdown_dynamic_throw_notContain.xlsx";
    static String DEPLOY_GROUP_2 = "src/main/resources/deploy/deploy_group_2.xlsx";
    static String DEPLOY_GROUP_2_THROW = "src/main/resources/deploy/deploy_group_2_throw.xlsx";


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
    public void b_buildSheet() throws Throwable {
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


}
