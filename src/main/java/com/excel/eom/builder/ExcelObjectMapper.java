package com.excel.eom.builder;


import com.excel.eom.exception.document.EOMWrongHeaderException;
import com.excel.eom.exception.object.EOMWrongObjectException;
import com.excel.eom.model.ColumnElement;
import com.excel.eom.util.*;
import com.excel.eom.util.callback.ColumnElementCallback;
import com.excel.eom.util.callback.ExcelColumnInfoCallback;
import com.excel.eom.util.callback.ExcelObjectInfoCallback;
import com.excel.eom.util.callback.RegionSettingCallback;
import com.sun.xml.internal.bind.v2.TODO;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 외부 제공 클래스
 * - buildSheet
 * - buildObject(List list)
 * */
public class ExcelObjectMapper {

    private static final Logger logger = LoggerFactory.getLogger(ExcelObjectMapper.class);

    private Class<?> clazz;
    private XSSFWorkbook book;
    private XSSFSheet sheet;
    private XSSFDataValidationHelper dvHelper;
    private Map<Field, ColumnElement> elements = new HashMap<>();
    private Map<Field, XSSFCellStyle> styles = new HashMap<>();
    private Map<Field, XSSFDataValidationConstraint> dropdowns = new HashMap<>();

    private ExcelObjectMapper() {

    }

    public static ExcelObjectMapper init() {
        return new ExcelObjectMapper();
    }

    public ExcelObjectMapper initModel(Class<?> clazz) {
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

    private void initElements()
            throws EOMWrongObjectException {
        if (this.clazz != null) {

            // instance validation
            try {
                Object instance = this.clazz.newInstance();
            } catch (InstantiationException e) {
                throw new EOMWrongObjectException();
            } catch (IllegalAccessException e) {
                throw new EOMWrongObjectException();
            }

            // TODO group validation

            ReflectionUtil.getFieldInfo(this.clazz, new ExcelColumnInfoCallback() {
                @Override
                public void getFieldInfo(Field field,
                                         String name,
                                         Integer index,
                                         Integer group,
                                         IndexedColors cellColor,
                                         BorderStyle borderStyle,
                                         IndexedColors borderColor) {
                    elements.put(field, new ColumnElement(name, index, group));

                    // init dropdown (sheet)
                    if(field.getType().isEnum() && sheet != null) {
                        if(dvHelper == null) {
                            dvHelper = new XSSFDataValidationHelper(sheet);
                        }

                        Object[] enums = field.getType().getEnumConstants();
                        List<String> items = new ArrayList<>();
                        for(Object e : enums) {
                            items.add(e.toString());
                        }
                        XSSFDataValidationConstraint dropdown = ExcelSheetUtil.getDropdown(dvHelper, items.toArray(new String[]{}));
                        dropdowns.put(field, dropdown);
                    }

                    // init style (book)
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
    public <T> void buildObject(List<T> objects) {
        if (this.book != null && this.sheet != null && this.clazz != null) {
            initElements();

            // [h]eader
            XSSFRow hRow = ExcelRowUtil.initRow(this.sheet, 0);
            setHeaderRow(hRow);

            // [b]ody
            // TODO exception listup
            for (int i = 0; i < objects.size(); i++) {
                T object = objects.get(i);
                XSSFRow bRow = ExcelRowUtil.initRow(this.sheet, i + 1);
                setBodyRow(bRow, object);
            }

            // [r]egion
            Map<Integer, List<Integer>> regionEndPointMapByGroup = checkRegion(objects);
            setRegion(regionEndPointMapByGroup);

            // [d]ropdown
            setEnum(objects.size());

        }
    }

    private void setHeaderRow(final XSSFRow row) {
        final XSSFWorkbook book = this.book;
        final Map<Field, ColumnElement> elements = this.elements;
        ReflectionUtil.getClassInfo(this.clazz, new ExcelObjectInfoCallback() {
            @Override
            public void getClassInfo(String name, IndexedColors cellColor, BorderStyle borderStyle, IndexedColors borderColor) {
                XSSFCellStyle cellStyle = ExcelCellUtil.getCellStyle(book);
                ExcelCellUtil.setCellColor(cellStyle, cellColor);
                ExcelCellUtil.setCellBorder(cellStyle, borderStyle, borderColor);
                ColumnElementUtil.getElement(elements, new ColumnElementCallback() {
                    @Override
                    public void getElement(Field field, ColumnElement element) {
                        XSSFCell cell = ExcelCellUtil.getCell(row, element.getIndex());
                        ExcelCellUtil.setCellValue(cell, element.getName());
                        ExcelCellUtil.setCellStyle(cell, cellStyle);
                    }
                });
            }
        });
    }

    private <T> void setBodyRow(XSSFRow row, T object) {
        ColumnElementUtil.getElement(this.elements, new ColumnElementCallback() {
            @Override
            public void getElement(Field field, ColumnElement element) {
                XSSFCell cell = ExcelCellUtil.getCell(row, element.getIndex());
                Object cellValue = null;
                try {
                    cellValue = field.get(object);
                } catch (IllegalAccessException e) {
                    // * DOESN'T OCCUR (initElement)
                }
                XSSFCellStyle style = styles.get(field);
                ExcelCellUtil.setCellValue(cell, cellValue, field);
                ExcelCellUtil.setCellStyle(cell, style);
            }
        });
    }

    private <T> Map<Integer, List<Integer>> checkRegion(List<T> objects) {
        Map<Integer, List<Integer>> regionEndPointMapByGroup = new HashMap<>(); /* Map<Group, List<EndPoint>> */

        // extract group column & sort
        Map<Field, ColumnElement> regionElements = ColumnElementUtil.extractElementByGroup(this.elements);
        List<Map.Entry<Field, ColumnElement>> regionElementsList = ColumnElementUtil.getElementListWithSort(regionElements);

        List nowEndPoints = new ArrayList<>(), preEndPoints = new ArrayList<>();
        Iterator<Map.Entry<Field, ColumnElement>> iterator = regionElementsList.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Field, ColumnElement> entry = iterator.next();
            Field field = entry.getKey();
            ColumnElement element = entry.getValue();

            boolean isGroup, isFirstGroup = false;
            if(element.getGroup() > 0) {
                if(regionEndPointMapByGroup.get(element.getGroup()) == null) {
                    isFirstGroup = true;
                    preEndPoints = nowEndPoints;
                    nowEndPoints = new ArrayList<>();
                    regionEndPointMapByGroup.put(element.getGroup(), nowEndPoints);
                } else {
                    isFirstGroup = false;
                }
                isGroup = true;
            } else {
                isGroup = false;
            }

            Object preValue = ReflectionUtil.getFieldValue(field, objects.get(0));
            for(int i=0; i<objects.size(); i++) {
                T object = objects.get(i);
                Object cellValue = ReflectionUtil.getFieldValue(field, object);

                if(isGroup) {
                    // group
                    if(isFirstGroup) {
                        if(preValue.equals(cellValue) == false) {
                            preValue = cellValue;
                            nowEndPoints.add((i + 1) - 1); // * pre cell merge (-1)
                        } else {
                            if(preEndPoints != null) {
                                if(preEndPoints.indexOf((i + 1) - 1) > -1) {
                                    nowEndPoints.add((i + 1) - 1); // * pre cell merge (-1)
                                }
                            }
                        }
                    } else {
                        // TODO exception check
                    }
                }
            }
            if(isFirstGroup) {
                nowEndPoints.add((objects.size() + 1) - 1); // * pre cell merge (-1)
            }
        }
        return regionEndPointMapByGroup;
    }

    private void setRegion(Map<Integer, List<Integer>> regionEndPointMapByGroup) {
        Iterator iterator = regionEndPointMapByGroup.entrySet().stream().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, List<Integer>> entry = (Map.Entry<Integer, List<Integer>>) iterator.next();
            Integer group = entry.getKey();
            List<Integer> endPoints = entry.getValue();
            ColumnElementUtil.getElement(elements, new ColumnElementCallback() {
                @Override
                public void getElement(Field field, ColumnElement element) {
                    if(element.getGroup() == group) {
                        int startPoint = 1;
                        for(int endPoint : endPoints) {
                            ExcelSheetUtil.addRegion(sheet, startPoint, endPoint, element.getIndex(), element.getIndex());
                            startPoint = ++endPoint;
                        }
                    }
                }
            });
        }
    }

    private void setEnum(int size) {
            Map<Field, ColumnElement> regionElements = ColumnElementUtil.extractElementByEnum(this.elements);
            List<Map.Entry<Field, ColumnElement>> regionElementsList = ColumnElementUtil.getElementListWithSort(regionElements);
            Iterator<Map.Entry<Field, ColumnElement>> iterator = regionElementsList.iterator();
            while (iterator.hasNext()) {
                Map.Entry<Field, ColumnElement> entry = iterator.next();
                Field field = entry.getKey();
                ColumnElement element = entry.getValue();
                for(int i=0; i<size; i++) {
                    CellRangeAddressList region = ExcelSheetUtil.getRegion(
                            (i + 1), (i + 1), element.getIndex(), element.getIndex());
                    ExcelSheetUtil.setDropdown(
                            this.sheet,
                            this.dvHelper,
                            this.dropdowns.get(field),
                            region);
                }
            }
    }

    /**
     * buildSheet (sheet -> object)
     *
     * */
    public <T> List<T> buildSheet()
            throws EOMWrongHeaderException {
        List<T> items = new ArrayList<>();

        if (this.sheet != null && this.clazz != null) {
            initElements();

            int sheetHeight = ExcelSheetUtil.getSheetHeight(this.sheet);

            // [h]eader
            XSSFRow hRow = ExcelRowUtil.initRow(this.sheet, 0);
            checkHeader(hRow);

            // [b]ody
            // TODO async 정상동작 확인필요
            // TODO exception listup
            Map<Field, Object> areaValues = new HashMap<>(); // region first cell value storage
            for (int i = 1; i < sheetHeight; i++) {
                items.add((T) getObjectByRow(this.sheet.getRow(i), areaValues, new RegionSettingCallback() {
                    @Override
                    public void setRegionInfo(Field field, Object instance, Object value) {
                        // TODO field & value compare

                        try {
                            field.set(instance, value);
                        } catch (IllegalAccessException e) {
                            // * DOESN'T OCCUR (initElement)
                        }
                    }
                }));
            }
        }
        return items;
    }

    private void checkHeader(XSSFRow row)
            throws EOMWrongHeaderException {
        final Map<Field, ColumnElement> elements = this.elements;
        ColumnElementUtil.getElement(elements, new ColumnElementCallback() {
            @Override
            public void getElement(Field field, ColumnElement element) {
                XSSFCell cell = ExcelCellUtil.getCell(row, element.getIndex());
                Object cellValue = ExcelCellUtil.getCellValue(cell);
                if(element.getName().equals(cellValue) == false) {
                    // * THROW WRONG_HEADER_EXCEPTION
                    throw new EOMWrongHeaderException();
                }
            }
        });
    }

    private Object getObjectByRow(XSSFRow row,
                                  Map<Field, Object> areaValues,
                                  RegionSettingCallback callback) {
        Object instance = null;
        try {
            instance = this.clazz.newInstance();
        } catch (InstantiationException e) {
            // * DOESN'T OCCUR (initElement)
        } catch (IllegalAccessException e) {
            // * DOESN'T OCCUR (initElement)
        }

        final XSSFSheet sheet = this.sheet;
        final Object copyInstance = instance;
        int rowIdx = ExcelRowUtil.getRowNum(row);
        ColumnElementUtil.getElement(this.elements, new ColumnElementCallback() {
            @Override
            public void getElement(Field field, ColumnElement element) {
                int colIdx = element.getIndex();
                XSSFCell cell = row.getCell(colIdx);
                Object cellValue = ExcelCellUtil.getCellValue(cell);

                // region value management
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
                callback.setRegionInfo(field, copyInstance, cellValue);
            }
        });
        return instance;
    }

}
