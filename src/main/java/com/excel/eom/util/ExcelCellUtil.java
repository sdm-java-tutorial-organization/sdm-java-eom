package com.excel.eom.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.lang.reflect.Field;

public class ExcelCellUtil {

    /**
     * getCell
     *
     * @param row
     * @param index
     * */
    public static XSSFCell getCell(XSSFRow row, int index) {
        if(row.getCell(index) != null) {
            return row.getCell(index);
        } else {
            return row.createCell(index);
        }
    }

    /**
     * getCellValue
     *
     * @param cell
     */
    public static Object getCellValue(XSSFCell cell) {
        Object result = null;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_FORMULA:
                result = cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                Double numericResult = cell.getNumericCellValue();
                result = numericResult.intValue();
                break;
            case Cell.CELL_TYPE_STRING:
                result = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                result = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_ERROR:
                result = cell.getErrorCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                break;
        }
        return result;
    }

    /**
     * setCellValue
     *
     * @param cell
     * @param value
     * */
    public static void setCellValue(XSSFCell cell, Object value) {
        cell.setCellValue((String) value);
    }

    /**
     * setCellValue
     *
     * @param cell
     * @param value
     * @param field
     * */
    public static void setCellValue(XSSFCell cell, Object value, Field field) {
        switch (field.getType().getSimpleName()) {
            case "String":
                cell.setCellValue((String) value);
                break;
            case "Integer":
                cell.setCellValue((Integer) value);
                break;
            case "Double":
                cell.setCellValue((Double) value);
                break;
        }
    }

}
