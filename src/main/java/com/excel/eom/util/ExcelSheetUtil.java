package com.excel.eom.util;

import com.excel.eom.builder.ExcelObjectMapper;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExcelSheetUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExcelSheetUtil.class);

    /**
     * initSheet
     *
     * @param book
     * @param name
     */
    public static XSSFSheet initSheet(XSSFWorkbook book,
                                      String name) {
        return book.createSheet(name);
    }

    /**
     * getSheet
     *
     * @param book
     * @param name
     */
    public static XSSFSheet getSheet(XSSFWorkbook book,
                                     String name) {
        return book.getSheet(name);
    }

    /**
     * getSheet
     *
     * @param book
     * @param index
     */
    public static XSSFSheet getSheet(XSSFWorkbook book,
                                     int index) {
        return book.getSheetAt(index);
    }

    /**
     * getSheetHeight
     *
     * @param sheet
     */
    public static int getSheetHeight(XSSFSheet sheet) {
        int rowCount = sheet.getPhysicalNumberOfRows();
        for (int i = 0; i < rowCount; i++) {
            XSSFRow row = ExcelRowUtil.getRow(sheet, i);
            if (row != null) {
                XSSFCell cell = ExcelCellUtil.getCell(row, 0);
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
        return rowCount;
    }

    /**
     * print
     *
     * @param sheet
     */
    public static void print(XSSFSheet sheet) {
        int rowCount = sheet.getPhysicalNumberOfRows();
        int columnCount = sheet.getRow(0).getPhysicalNumberOfCells();
        for (int i = 0; i < rowCount; i++) {
            XSSFRow row = sheet.getRow(i);
            List<Object> rowStorage = new ArrayList<>();
            for (int j = 0; j < columnCount; j++) {
                XSSFCell cell = row.getCell(j);
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

    /**
     * getMergedRegion
     *
     * @param sheet
     * @param rowIdx
     * @param colIdx
     */
    public static CellRangeAddress getMergedRegion(XSSFSheet sheet,
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
    public static void addRegion(XSSFSheet sheet,
                                 int firstRow,
                                 int lastRow,
                                 int firstColumn,
                                 int lastColumn) {
        CellRangeAddress area = new CellRangeAddress(firstRow, lastRow, firstColumn, lastColumn);
        sheet.addMergedRegion(area);
    }

}
