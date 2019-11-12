package com.excel.eom.util;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExcelSheetUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExcelSheetUtil.class);

    /**
     * print
     *
     * @param sheet
     */
    public static void print(Sheet sheet) {
        int rowCount = sheet.getPhysicalNumberOfRows();
        Row firstRow = sheet.getRow(0);
        if(firstRow != null) {
            int columnCount = sheet.getRow(0).getPhysicalNumberOfCells();
            for (int i = 0; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                List<Object> rowStorage = new ArrayList<>();
                for (int j = 0; j < columnCount; j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        Object value = ExcelCellUtil.getCellValue(cell);
                        rowStorage.add(value);
                    } else {
                        rowStorage.add(null);
                    }
                }
                System.out.println(Arrays.asList(rowStorage.toArray()));
            }
        }
    }

    /**
     * initSheet
     *
     * @param book
     * @param name
     */
    public static Sheet initSheet(Workbook book,
                                  String name) {
        return book.createSheet(name);
    }

    /**
     * getSheet
     *
     * @param book
     * @param name
     */
    public static Sheet getSheet(Workbook book,
                                     String name) {
        return book.getSheet(name);
    }

    /**
     * getSheet
     *
     * @param book
     * @param index
     */
    public static Sheet getSheet(Workbook book,
                                     int index) {
        return book.getSheetAt(index);
    }

    /**
     * getSheets
     *
     * @param book
     * */
    public static List<Sheet> getSheets(Workbook book) {
        List<Sheet> result = new ArrayList<>();
        int sheetCount = book.getNumberOfSheets();
        for(int i=0; i<sheetCount; i++) {
            result.add(ExcelSheetUtil.getSheet(book, i));
        }
        return result;
    }

    /**
     * getSheetNames
     *
     * @param book
     * */
    public static List<String> getSheetNames(Workbook book) {
        List<String> result = new ArrayList<>();
        int sheetCount = book.getNumberOfSheets();
        for(int i=0; i<sheetCount; i++) {
            result.add(ExcelSheetUtil.getSheet(book, i).getSheetName());
        }
        return result;
    }

    /**
     * getSheetName
     *
     * @param sheet
     * */
    public static String getSheetName(Sheet sheet) {
        return sheet.getSheetName();
    }

    /**
     * getSheetHeight
     *
     * @param sheet
     */
    public static int getSheetHeight(Sheet sheet) {
        int rowCount = sheet.getPhysicalNumberOfRows();
        return rowCount;
        /*for (int i = 0; i < rowCount; i++) {
            Row row = ExcelRowUtil.getRow(sheet, i);
            if (row != null) {
                Cell cell = ExcelCellUtil.getCell(row, 0);
                if (cell != null) {
                    String value = (String) ExcelCellUtil.getCellValue(cell);
                    if (value == null || value.equals("")) {
                        CellRangeAddress area = getMergedRegion(sheet, i, 0);
                        if (area == null || area.getFirstRow() == i) {
                            return i;
                        }
                    }
                } else {
                    return i;
                }
            } else {
                return i;
            }
        }
        return rowCount;*/
    }

    /**
     * getMergedRegion
     *
     * @param sheet
     * @param rowIdx
     * @param colIdx
     */
    public static CellRangeAddress getMergedRegion(Sheet sheet,
                                                   int rowIdx,
                                                   int colIdx) {
        for (int i = 0; i < sheet.getNumMergedRegions(); ++i) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            if (
                    rowIdx >= range.getFirstRow()
                            &&
                            rowIdx <= range.getLastRow()
                            &&
                            colIdx >= range.getFirstColumn()
                            &&
                            colIdx <= range.getLastColumn()
            ) {
                return range;
            }
        }
        return null;
    }

    /**
     * addRegion
     *
     * @param sheet
     * @param firstRow
     * @param lastRow
     * @param firstColumn
     * @param lastColumn
     */
    public static void addRegion(Sheet sheet,
                                 int firstRow,
                                 int lastRow,
                                 int firstColumn,
                                 int lastColumn) {
        CellRangeAddress area = new CellRangeAddress(firstRow, lastRow, firstColumn, lastColumn);
        sheet.addMergedRegion(area);
    }

    public static CellRangeAddressList getRegion(int firstRow,
                                                 int lastRow,
                                                 int firstColumn,
                                                 int lastColumn) {
        return new CellRangeAddressList(firstRow, lastRow, firstColumn, lastColumn);
    }

    public static DataValidationConstraint getDropdown(DataValidationHelper dvHelper,
                                                       String[] items) {
        return dvHelper.createExplicitListConstraint(items);
    }

    public static void setDropdown(Sheet sheet,
                                   DataValidationHelper dvHelper,
                                   DataValidationConstraint dropdown,
                                   CellRangeAddressList regions) {
        DataValidation inputDataValidation = dvHelper.createValidation(dropdown, regions);
        inputDataValidation.setShowErrorBox(true);
        inputDataValidation.setSuppressDropDownArrow(true);
        sheet.addValidationData(inputDataValidation);
    }

    public static void createFreezePane(Sheet sheet, int fixedColumnCount, int fixedRowCount) {
        sheet.createFreezePane(fixedColumnCount, fixedRowCount);
    }

}
