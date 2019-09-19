package com.excel.eom.util;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;

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
        if(field.getType().isEnum()) {
            // Enum
            cell.setCellValue(value.toString());
        } else {
            // Class
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

    /**
     * getCellStyle
     *
     * @param book
     * */
    public static XSSFCellStyle getCellStyle(XSSFWorkbook book) {
        return book.createCellStyle();
    }

    /**
     * getCellStyle
     *
     * @param cell
     * @param cellStyle
     * */
    public static void setCellStyle(XSSFCell cell, XSSFCellStyle cellStyle) {
        cell.setCellStyle(cellStyle);
    }

    /**
     * setCellColor
     *
     * @param cellStyle
     * @param color
     * */
    public static void setCellColor(XSSFCellStyle cellStyle, IndexedColors color) {
        cellStyle.setFillForegroundColor(color.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    }

    /**
     * setCellColor
     *
     * @param cellStyle
     * @param alignment
     * */
    public static void setAlignment(XSSFCellStyle cellStyle, short alignment) {
        cellStyle.setAlignment(alignment);
    }

    /**
     * setCellBorder
     *
     * @param cellStyle
     * @param borderStyle
     * @param borderColor
     * */
    public static void setCellBorder(XSSFCellStyle cellStyle, BorderStyle borderStyle, IndexedColors borderColor) {
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBottomBorderColor(borderColor.getIndex());
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setLeftBorderColor(borderColor.getIndex());
        cellStyle.setBorderRight(borderStyle);
        cellStyle.setRightBorderColor(borderColor.getIndex());
        cellStyle.setBorderTop(borderStyle);
        /*cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);*/
    }

    /**
     * getFont
     *
     * @param book
     * */
    public static XSSFFont getFont(XSSFWorkbook book) {
        return book.createFont();
    }

    /**
     * setFont
     *
     * @param style
     * @param font
     * */
    public static void setFont(XSSFCellStyle style, XSSFFont font) {
        style.setFont(font);
    }

}
