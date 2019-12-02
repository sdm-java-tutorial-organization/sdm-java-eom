package com.excel.eom.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.lang.reflect.Field;

public class ExcelCellUtil {

    /**
     * getCell
     *
     * @param row
     * @param index
     * */
    public static Cell getCell(Row row, int index) {
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
    public static Object getCellValue(Cell cell) {
        if(cell == null) {
            return null;
        }

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
    public static void setCellValue(Cell cell, Object value) {
        cell.setCellValue((String) value);
    }

    /**
     * setCellValue
     *
     * @param cell
     * @param value
     * @param type - SimpleClassName or Field
     * */
    public static void setCellValue(Cell cell, Object value, Object type) {
        if(type instanceof Field) {
            Field field = (Field) type;
            if(field.getType().isEnum()) {
                // Enum
                value = value != null ? value.toString() : value;
                cell.setCellValue((String) value);
            } else {
                // Class
                switch (field.getType().getSimpleName()) {
                    case "String":
                        if(value != null) {
                            cell.setCellValue((String) value);
                        } else {
                            cell.setCellValue((String) "");
                        }
                        break;
                    case "int":
                    case "Integer":
                        if(value != null) {
                            cell.setCellValue((Integer) value);
                        } else {
                            cell.setCellValue(0);
                        }
                        break;
                    case "long":
                    case "Long":
                        if(value != null) {
                            cell.setCellValue(((Long) value).intValue());
                        } else {
                            cell.setCellValue(0);
                        }
                        break;
                    case "double":
                    case "Double":
                        if(value != null) {
                            cell.setCellValue((Double) value);
                        } else {
                            cell.setCellValue(0);
                        }
                        break;
                    case "float":
                    case "Float":
                        break;
                }
            }
        }
        // == Field 와 다른 DataType ==
        else if(type instanceof String) {
            String className = (String) type;
            switch (className) {
                case "String":
                    cell.setCellValue((String) value);
                    break;
                case "int":
                case "Integer":
                    cell.setCellValue((Integer) value);
                    break;
                case "long":
                case "Long":
                    cell.setCellValue(((Long) value).intValue());
                    break;
                case "double":
                case "Double":
                    cell.setCellValue((Double) value);
                    break;
                case "float":
                case "Float":
                    break;
            }
        }

    }

    /**
     * (XSSF) getCellStyle
     *
     * @param book
     * */
    public static CellStyle getCellStyle(Workbook book) {
        return book.createCellStyle();
    }

    /**
     * getCellStyle
     *
     * @param cell
     * @param cellStyle
     * */
    public static void setCellStyle(Cell cell, CellStyle cellStyle) {
        cell.setCellStyle(cellStyle);
    }

    /**
     * setCellColor
     *
     * @param cellStyle
     * @param color
     * */
    public static void setCellColor(CellStyle cellStyle, IndexedColors color) {
        cellStyle.setFillForegroundColor(color.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    }

    /**
     * setCellColor
     *
     * @param cellStyle
     * @param alignment
     * */
    public static void setAlignment(CellStyle cellStyle, short alignment) {
        cellStyle.setAlignment(alignment);
    }

    /**
     * (XSSF) setCellBorder
     *
     * @param cellStyle
     * @param borderStyle
     * @param borderColor
     * */
    public static void setCellBorder(CellStyle cellStyle, short borderStyle, IndexedColors borderColor) {
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
     * (XSSF) getFont
     *
     * @param book
     * */
    public static Font getFont(Workbook book) {
        return book.createFont();
    }

    /**
     * setFont
     *
     * @param style
     * @param font
     * */
    public static void setFont(CellStyle style, XSSFFont font) {
        style.setFont(font);
    }

}
