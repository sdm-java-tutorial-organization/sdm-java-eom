package com.excel.eom.builder;

import com.excel.eom.annotation.ExcelColumn;
import com.excel.eom.annotation.ExcelObject;
import com.excel.eom.tutorial.ExcelObjectDemo;
import com.excel.eom.tutorial.Planet;
import com.excel.eom.util.ExcelFileUtil;
import com.excel.eom.util.ExcelRegionUtil;
import com.excel.eom.util.ExcelSheetUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ExcelObjectMapperTest {

    File file = new File("src/main/resources/sample/sample_multiple.xlsx");
    File fileGroup = new File("src/main/resources/sample/sample_multiple.xlsx");

    @Test
    public void buildObjectWithDropdown() throws Throwable {
        List<ExcelObjectDemo> items = Arrays.asList(
                new ExcelObjectDemo("marvel", Planet.Earth, "nameA", 1, 101, Planet.Earth),
                new ExcelObjectDemo("marvel", Planet.Earth, "nameB", 2, 102, Planet.Earth),
                new ExcelObjectDemo("marvel", Planet.Earth, "nameC", 3, 103, Planet.Asgard),
                new ExcelObjectDemo("sony", Planet.Earth, "nameD", 4, 104, Planet.Earth),
                new ExcelObjectDemo("sony", Planet.Earth, "nameE", 5, 105, Planet.Earth),
                new ExcelObjectDemo("sony", Planet.Earth, "nameF", 6, 106, Planet.Earth),
                new ExcelObjectDemo("sony", Planet.Earth, "nameG", 7, 107, Planet.Earth),
                new ExcelObjectDemo("desney", Planet.Earth, "nameH", 8, 108, Planet.Titan),
                new ExcelObjectDemo("desney", Planet.Earth, "nameI", 9, 109, Planet.Earth),
                new ExcelObjectDemo("desney", Planet.Earth, "nameJ", 10, 110, Planet.Earth),
                new ExcelObjectDemo("desney", Planet.Earth, "nameK", 11, 111, Planet.Earth)
        );

        XSSFWorkbook book = new XSSFWorkbook();
        XSSFSheet sheet = ExcelSheetUtil.initSheet(book, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectDemo.class)
                .initBook(book)
                .initSheet(sheet)
                .buildObject(items);

        // print-sheet TODO value 확인안됨 -> region 확인
        /*assertEquals(sheet.getNumMergedRegions(), 3);*/
        ExcelRegionUtil.print(sheet);

        // write
        ExcelFileUtil.writeExcel(book, "src/main/resources/deploy/deploy_dropdown.xlsx");
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

    @Test
    public void buildObjectWithTwoDepth() throws Throwable {
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

        XSSFWorkbook book = new XSSFWorkbook();
        XSSFSheet sheet = ExcelSheetUtil.initSheet(book, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectTwoDepth.class)
                .initBook(book)
                .initSheet(sheet)
                .buildObject(items);

        // print-sheet TODO value 확인안됨 -> region 확인
        ExcelRegionUtil.print(sheet);

        // write
        ExcelFileUtil.writeExcel(book, "src/main/resources/deploy/deploy_twodepth.xlsx");
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

        @ExcelColumn(name = "PARENT", group = 2, index = 0)
        public String parentGroup;

        @ExcelColumn(name = "PARENT_NAME", group = 2, index = 1)
        public String parentGroupName;

        @ExcelColumn(name = "CHILD", group = 1, index = 2)
        public String childGroup;

        @ExcelColumn(name = "NAME", index = 3)
        public String name;

        @ExcelColumn(name = "COUNT", index = 4)
        public Integer count;

    }

    @Test
    public void buildObjectWithThreeDepth() throws Throwable {
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
        XSSFSheet sheet = ExcelSheetUtil.initSheet(book, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectThreeDepth.class)
                .initBook(book)
                .initSheet(sheet)
                .buildObject(items);

        // print-sheet TODO value 확인안됨 -> region 확인
        ExcelRegionUtil.print(sheet);

        // write
        ExcelFileUtil.writeExcel(book, "src/main/resources/deploy/deploy_threedepth.xlsx");
    }

    @Test
    public void test() throws Throwable {
        /*File file = new File("src/main/resources/deploy/deploy_multiple.xlsx");
        XSSFWorkbook bookDeploy = ExcelFileUtil.readExcelByFile(file);
        XSSFSheet sheetDeploy = ExcelSheetUtil.getSheet(bookDeploy, 0);

        // print-sheet
        ExcelSheetUtil.print(sheetDeploy);*/
    }

    @Test
    public void buildSheet() throws Throwable {
        /*XSSFWorkbook book = ExcelFileUtil.readExcelByFile(file);
        XSSFSheet sheet = ExcelSheetUtil.getSheet(book, 0);
        ExcelSheetUtil.print(sheet);

        // print-sheet
        List<ExcelObjectDemo> items = ExcelObjectMapper.init()
                .initModel(ExcelObjectDemo.class)
                .initBook(book)
                .initSheet(sheet)
                .buildSheet();

        // print-object
        for(ExcelObjectDemo item : items) {
            System.out.println(item.toString());
        }*/
    }

    @Test
    public void buildSheetWithGroup() throws Throwable {
        /*XSSFWorkbook book = ExcelFileUtil.readExcelByFile(fileGroup);
        XSSFSheet sheet = ExcelSheetUtil.getSheet(book, 0);

        // print-sheet
        ExcelSheetUtil.print(sheet);

        List<ExcelObjectDemo> items = ExcelObjectMapper.init()
                .initModel(ExcelObjectDemo.class)
                .initBook(book)
                .initSheet(sheet)
                .buildSheet();

        // print-object
        for(ExcelObjectDemo item : items) {
            System.out.println(item.toString());
        }*/
    }


}
