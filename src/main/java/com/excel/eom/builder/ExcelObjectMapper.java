package com.excel.eom.builder;


import com.excel.eom.model.ColumnElement;
import com.excel.eom.util.*;
import com.excel.eom.util.callback.ColumnElementCallback;
import com.excel.eom.util.callback.ExcelColumnInfoCallback;
import com.excel.eom.util.callback.ExcelObjectInfoCallback;
import com.excel.eom.util.callback.RegionSettingCallback;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelObjectMapper {

    private static final Logger logger = LoggerFactory.getLogger(ExcelObjectMapper.class);

    private Class<?> clazz;
    private XSSFWorkbook book;
    private XSSFSheet sheet;
    private Map<Field, ColumnElement> elements = new HashMap<>();
    private Map<Field, XSSFCellStyle> styles = new HashMap<>();

    private ExcelObjectMapper() {

    }

    public static ExcelObjectMapper init() {
        return new ExcelObjectMapper();
    }

    public ExcelObjectMapper initModel(Class<?> clazz) throws Throwable {
        this.clazz = clazz;
        return this;
    }

    public ExcelObjectMapper initSheet(XSSFSheet sheet) {
        this.sheet = sheet;
        return this;
    }

    public ExcelObjectMapper initBook(XSSFWorkbook book) {
        this.book = book;
        return this;
    }

    private void initElements() throws Throwable {
        if (this.clazz != null) {
            ReflectionUtil.getFieldInfo(this.clazz, new ExcelColumnInfoCallback() {
                @Override
                public void getFieldInfo(Field field, String name, Integer index, Integer group, IndexedColors cellColor, BorderStyle borderStyle, IndexedColors borderColor) {
                    elements.put(field, new ColumnElement(name, index, group));
                    if(book != null) {
                        XSSFCellStyle cellStyle = ExcelCellUtil.getCellStyle(book);
                        ExcelCellUtil.setCellColor(cellStyle, cellColor);
                        ExcelCellUtil.setCellBorder(cellStyle, borderStyle, borderColor);
                        styles.put(field, cellStyle);
                    }
                }
            });
        }
    }

    /**
     * buildObject (object -> sheet)
     *
     * @param objects
     * */
    public <T> void buildObject(List<T> objects) throws Throwable {
        if (this.book != null && this.sheet != null && this.clazz != null) {
            initElements();

            // [h]eader
            XSSFRow hRow = ExcelRowUtil.initRow(this.sheet, 0);
            setHeaderRow(hRow);

            // [b]ody
            for (int i = 0; i < objects.size(); i++) {
                T object = objects.get(i);
                XSSFRow bRow = ExcelRowUtil.initRow(this.sheet, i + 1);
                setBodyRow(bRow, object);
            }

            // [r]egion
            Map<Field, ColumnElement> groupElements = this.elements.entrySet().stream()
                    .filter(element -> element.getValue().getGroup() > 0)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            Iterator<Map.Entry<Field, ColumnElement>> iterator = groupElements.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Field, ColumnElement> entry = iterator.next();
                Field field = entry.getKey();
                ColumnElement element = entry.getValue();

                // TODO header 제외

                Object preValue = null;
                int count = 0;
                for(int i=0; i<objects.size(); i++) {
                    T object = objects.get(i);
                    Object cellValue = field.get(object);

                    if(preValue == null) {
                        // init
                        preValue = cellValue;
                        count = 0;
                    } else {
                        if(preValue.equals(cellValue) == false) {
                            /*logger.debug(String.format("fr : %d, lr : %d, fc : %d, lc : %d",
                                    (i - 1 + 1) - count,
                                    (i - 1 + 1),
                                    element.getIndex(),
                                    element.getIndex()));*/
                            ExcelSheetUtil.addRegion(this.sheet,
                                    (i - 1 + 1) - count , (i - 1 + 1),
                                    element.getIndex(), element.getIndex());
                            // init
                            preValue = cellValue;
                            count = 0;
                        } else {
                            // same
                            count++;
                        }
                    }
                }
                if(count > 0) {
                    /*logger.debug(String.format("fr : %d, lr : %d, fc : %d, lc : %d",
                            (objects.size() - 1 + 1) - count,
                            (objects.size() - 1 + 1),
                            element.getIndex(),
                            element.getIndex()));*/
                    ExcelSheetUtil.addRegion(this.sheet,
                            (objects.size() - 1 + 1) - count , (objects.size() - 1 + 1),
                            element.getIndex(), element.getIndex());
                }
            }
        }
    }

    private void setHeaderRow(final XSSFRow row) throws Throwable {
        final XSSFWorkbook book = this.book;
        final Map<Field, ColumnElement> elements = this.elements;
        ReflectionUtil.getClassInfo(this.clazz, new ExcelObjectInfoCallback() {
            @Override
            public void getClassInfo(String name, IndexedColors cellColor, BorderStyle borderStyle, IndexedColors borderColor) throws Throwable {
                XSSFCellStyle cellStyle = ExcelCellUtil.getCellStyle(book);
                ExcelCellUtil.setCellColor(cellStyle, cellColor);
                ExcelCellUtil.setCellBorder(cellStyle, borderStyle, borderColor);
                ColumnElementUtil.getElement(elements, new ColumnElementCallback() {
                    @Override
                    public void getElement(Field field, ColumnElement element) throws Throwable {
                        XSSFCell cell = ExcelCellUtil.getCell(row, element.getIndex());
                        ExcelCellUtil.setCellValue(cell, element.getName());
                        ExcelCellUtil.setCellStyle(cell, cellStyle);
                    }
                });
            }
        });
    }

    private <T> void setBodyRow(XSSFRow row, T object) throws Throwable {
        ColumnElementUtil.getElement(this.elements, new ColumnElementCallback() {
            @Override
            public void getElement(Field field, ColumnElement element) throws Throwable {
                XSSFCell cell = ExcelCellUtil.getCell(row, element.getIndex());
                Object cellValue = field.get(object);
                XSSFCellStyle style = styles.get(field);
                ExcelCellUtil.setCellValue(cell, cellValue, field);
                ExcelCellUtil.setCellStyle(cell, style);
            }
        });
    }

    /**
     * buildSheet (sheet -> object)
     *
     * */
    public <T> List<T> buildSheet() throws Throwable {
        List<T> items = new ArrayList<>();

        if (this.sheet != null && this.clazz != null) {
            initElements();

            int sheetHeight = ExcelSheetUtil.getSheetHeight(this.sheet);

            // [h]eader
            // TODO only validation
            /*XSSFRow hRow = ExcelRowUtil.initRow(this.sheet, 0);
            setHeaderRow(hRow);*/

            // [b]ody
            Map<Field, Object> areaValues = new HashMap<>(); // region first cell value storage
            for (int i = 1; i < sheetHeight; i++) {
                items.add((T) getObjectByRow(this.sheet.getRow(i), areaValues, new RegionSettingCallback() {
                    @Override
                    public void setRegionInfo(Field field, Object instance, Object value) throws Throwable{
                        field.set(instance, value);
                    }
                }));
            }
        }
        return items;
    }

    private Object getObjectByRow(XSSFRow row,
                                  Map<Field, Object> areaValues,
                                  RegionSettingCallback callback) throws Throwable {
        Object instance = this.clazz.newInstance();

        final XSSFSheet sheet = this.sheet;
        int rowIdx = ExcelRowUtil.getRowNum(row);
        ColumnElementUtil.getElement(this.elements, new ColumnElementCallback() {
            @Override
            public void getElement(Field field, ColumnElement element) throws Throwable {
                int colIdx = element.getIndex();
                XSSFCell cell = row.getCell(colIdx);
                Object cellValue = ExcelCellUtil.getCellValue(cell);
                if (element.getGroup() > 0) {
                    CellRangeAddress region = ExcelSheetUtil.getMergedRegion(sheet, rowIdx, colIdx);
                    if (region != null) {
                        if(region.getFirstRow() == rowIdx) {
                            areaValues.put(field, cellValue);
                        } else {
                            cellValue = areaValues.get(field);
                        }
                    }
                }
                callback.setRegionInfo(field, instance, cellValue);
            }
        });
        return instance;
    }

}
