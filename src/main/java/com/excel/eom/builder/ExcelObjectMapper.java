package com.excel.eom.builder;


import com.excel.eom.exception.EOMBodyException;
import com.excel.eom.exception.EOMHeaderException;
import com.excel.eom.exception.EOMObjectException;
import com.excel.eom.exception.body.EOMNotContainException;
import com.excel.eom.exception.body.EOMNotNullException;
import com.excel.eom.model.ColumnElement;
import com.excel.eom.model.Dropdown;
import com.excel.eom.util.*;
import com.excel.eom.util.callback.ColumnElementCallback;
import com.excel.eom.util.callback.ExcelColumnInfoCallback;
import com.excel.eom.util.callback.ExcelObjectInfoCallback;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

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
    private Map<String, Map<String, Object>> options = new HashMap<>();

    private ExcelObjectMapper() {

    }

    public static ExcelObjectMapper init() {
        return new ExcelObjectMapper();
    }

    public ExcelObjectMapper initModel(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    public ExcelObjectMapper initBook(XSSFWorkbook book) {
        this.book = book;
        return this;
    }

    public ExcelObjectMapper initSheet(XSSFSheet sheet) {
        this.sheet = sheet;
        return this;
    }

    public ExcelObjectMapper initDropDowns(Dropdown... dropdowns) {
        for (Dropdown dropdown : dropdowns) {
            this.options.put(dropdown.getKey(), dropdown.getOptions());
        }
        return this;
    }

    private void initElements()
            throws EOMObjectException {
        if (this.clazz != null) {

            // instance validation
            try {
                Object instance = this.clazz.newInstance();
            } catch (InstantiationException e) {
                throw new EOMObjectException();
            } catch (IllegalAccessException e) {
                throw new EOMObjectException();
            }

            // TODO group validation ( ? )


            ReflectionUtil.getFieldInfo(this.clazz, new ExcelColumnInfoCallback() {
                @Override
                public void getFieldInfo(// == info ==
                                         Field field,
                                         String name,
                                         Integer index,
                                         Integer group,
                                         String dropdown,
                                         Boolean nullable,
                                         // == style ==
                                         Integer width,
                                         Short alignment,
                                         IndexedColors cellColor,
                                         BorderStyle borderStyle,
                                         IndexedColors borderColor) {
                    elements.put(field, new ColumnElement(name, index, group, dropdown, nullable));
                }
            });
        }
    }

    /**
     * presetSheet ( object -> sheet )
     *
     * */
    private void presetSheet() {
        if(this.clazz != null && this.sheet != null) {
            ReflectionUtil.getFieldInfo(this.clazz, new ExcelColumnInfoCallback() {
                @Override
                public void getFieldInfo(Field field,
                                         String name,
                                         Integer index,
                                         Integer group,
                                         String dropdown,
                                         Boolean nullable,
                                         Integer width,
                                         Short alignment,
                                         IndexedColors cellColor,
                                         BorderStyle borderStyle,
                                         IndexedColors borderColor) {
                    // init width (sheet)
                    sheet.setColumnWidth(index, width * 100 * 3);

                    // init static dropdown (sheet)
                    if(field.getType().isEnum()) {
                        if(dvHelper == null) {
                            dvHelper = new XSSFDataValidationHelper(sheet);
                        }

                        Object[] enums = field.getType().getEnumConstants();
                        List<String> items = new ArrayList<>();
                        for(Object e : enums) {
                            items.add(e.toString());
                        }
                        dropdowns.put(field, ExcelSheetUtil.getDropdown(dvHelper, items.toArray(new String[]{})));
                    }

                    // init dynamic dropdown (sheet)
                    if(dropdown.equals("") == false) {
                        if(dvHelper == null) {
                            dvHelper = new XSSFDataValidationHelper(sheet);
                        }
                        // TODO null check
                        Map<String, Object> option = options.get(dropdown);
                        List<String> items = new ArrayList(option.keySet());
                        dropdowns.put(field, ExcelSheetUtil.getDropdown(dvHelper, items.toArray(new String[]{})));
                    }

                    // init style (book)
                    if(book != null) {
                        XSSFCellStyle cellStyle = ExcelCellUtil.getCellStyle(book);
                        ExcelCellUtil.setCellColor(cellStyle, cellColor);
                        ExcelCellUtil.setCellBorder(cellStyle, borderStyle, borderColor);
                        ExcelCellUtil.setAlignment(cellStyle, alignment);
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
    public <T> void buildObject(List<T> objects) throws EOMBodyException {
        if (this.book != null && this.sheet != null && this.clazz != null) {
            initElements();
            presetSheet();

            // [h]eader
            XSSFRow hRow = ExcelRowUtil.initRow(this.sheet, 0);
            setHeaderRow(hRow);

            // [b]ody
            EOMBodyException bodyException = new EOMBodyException();
            for (int i = 0; i < objects.size(); i++) {
                T object = objects.get(i);
                XSSFRow bRow = ExcelRowUtil.initRow(this.sheet, i + 1);
                setBodyRow(bRow, object, bodyException);
            }

            // [r]egion
            Map<Integer, List<Integer>> regionEndPointMapByGroup = checkRegion(objects);
            setRegion(regionEndPointMapByGroup);

            // [d]ropdown
            setDropDown(objects.size());

            if(bodyException.countDetail() > 0) {
                throw bodyException;
            }
        }
    }

    /**
     * setHeaderRow
     *
     * */
    private void setHeaderRow(final XSSFRow row) {
        final XSSFWorkbook book = this.book;
        final Map<Field, ColumnElement> elements = this.elements;
        ReflectionUtil.getClassInfo(this.clazz, new ExcelObjectInfoCallback() {
            @Override
            public void getClassInfo(String name, IndexedColors cellColor, BorderStyle borderStyle, IndexedColors borderColor) {
                XSSFCellStyle cellStyle = ExcelCellUtil.getCellStyle(book);
                XSSFFont font = ExcelCellUtil.getFont(book);

                // style - static
                font.setBold(true);
                ExcelSheetUtil.createFreezePane(sheet, 1);
                ExcelCellUtil.setAlignment(cellStyle, CellStyle.ALIGN_CENTER);
                ExcelCellUtil.setFont(cellStyle, font);

                // style - dynamic
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

    private <T> void setBodyRow(XSSFRow row, T object, EOMBodyException bodyException) {
        ColumnElementUtil.getElement(this.elements, new ColumnElementCallback() {
            @Override
            public void getElement(Field field, ColumnElement element) {
                XSSFCell cell = ExcelCellUtil.getCell(row, element.getIndex());
                Object cellValue = null;
                String cellType = null;

                try {
                    cellValue = field.get(object);
                } catch (IllegalAccessException e) {
                    // * DOESN'T OCCUR (initElement)
                }

                // nullable
                if(element.getNullable() == false) {
                    if(cellValue == null || cellValue.equals("")) {
                        EOMNotNullException e = new EOMNotNullException(row.getRowNum(), element.getIndex());
                        bodyException.addDetail(e);
                    }
                }

                // dropdown - satic
                if(field.getType().isEnum()) {
                    if(cellValue == null) {
                        EOMNotContainException e = new EOMNotContainException(row.getRowNum(), element.getIndex());
                        bodyException.addDetail(e);
                    }
                }

                // dropdown - dynamic
                if(element.getDropdown().equals(ColumnElement.INIT_DROPDOWN) == false) {
                    Iterator iterator = options.get(element.getDropdown()).entrySet().iterator();
                    boolean isContain = false;
                    while (iterator.hasNext()) {
                        Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                        Object value = entry.getValue();
                        if(cellValue.equals(value)) {
                            cellValue = entry.getKey();
                            cellType = entry.getKey().getClass().getSimpleName();
                            isContain = true;
                        }
                    }
                    if(isContain == false) {
                        EOMNotContainException e = new EOMNotContainException(row.getRowNum(), element.getIndex());
                        bodyException.addDetail(e);
                    }
                }

                XSSFCellStyle style = styles.get(field);
                ExcelCellUtil.setCellValue(cell, cellValue, (cellType != null ? cellType : field));
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

    private void setDropDown(int size) {
            Map<Field, ColumnElement> regionElements = ColumnElementUtil.extractElementByDropdown(this.elements);
            List<Map.Entry<Field, ColumnElement>> regionElementsList = ColumnElementUtil.getElementListWithSort(regionElements);
            Iterator<Map.Entry<Field, ColumnElement>> iterator = regionElementsList.iterator();
            while (iterator.hasNext()) {
                Map.Entry<Field, ColumnElement> entry = iterator.next();
                Field field = entry.getKey();
                ColumnElement element = entry.getValue();
                if(field.getType().isEnum()) {
                    // [1] static (enum)
                    for(int i=0; i<size; i++) {
                        CellRangeAddressList region = ExcelSheetUtil.getRegion(
                                (i + 1), (i + 1), element.getIndex(), element.getIndex());
                        ExcelSheetUtil.setDropdown(
                                this.sheet,
                                this.dvHelper,
                                this.dropdowns.get(field),
                                region);
                    }
                } else {
                    // [2] dynamic
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
    }

    /**
     * buildSheet (sheet -> object)
     *
     * */
    public <T> List<T> buildSheet()
            throws EOMHeaderException, EOMBodyException {
        List<T> items = new ArrayList<>();

        if (this.sheet != null && this.clazz != null) {
            initElements();
            int sheetHeight = ExcelSheetUtil.getSheetHeight(this.sheet);
            if(sheetHeight > 1) {

                // [h]eader - haederException
                XSSFRow hRow = ExcelRowUtil.getRow(this.sheet, 0);
                checkHeader(hRow);

                // [b]ody - bodyException
                EOMBodyException bodyException = new EOMBodyException(items);
                Map<Field, Object> areaValues = new HashMap<>(); // region first cell value storage
                for (int i = 1; i < sheetHeight; i++) {
                    // empty valid
                    if(ExcelRowUtil.isEmptyRow(this.sheet.getRow(i)) == false) {
                        items.add((T) getObjectByRow(this.sheet.getRow(i), areaValues, bodyException));
                    }
                }

                if(bodyException.countDetail() > 0) {
                    throw bodyException;
                }
            }
        }
        return items;
    }

    private void checkHeader(XSSFRow row)
            throws EOMHeaderException {
        final Map<Field, ColumnElement> elements = this.elements;
        ColumnElementUtil.getElement(elements, new ColumnElementCallback() {
            @Override
            public void getElement(Field field, ColumnElement element) {
                XSSFCell cell = ExcelCellUtil.getCell(row, element.getIndex());
                Object cellValue = ExcelCellUtil.getCellValue(cell);
                if(element.getName().equals(cellValue) == false) {
                    // * THROW WRONG_HEADER_EXCEPTION
                    throw new EOMHeaderException();
                }
            }
        });
    }

    private Object getObjectByRow(XSSFRow row,
                                  Map<Field, Object> areaValues,
                                  EOMBodyException bodyException) {
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

                // nullable
                if(element.getNullable() == false) {
                    if(cellValue == null || cellValue.equals("")) {
                        EOMNotNullException e = new EOMNotNullException(row.getRowNum(), element.getIndex());
                        bodyException.addDetail(e);
                    }
                }

                // dropdown - static
                if(field.getType().isEnum()) {
                    Enum[] enums = (Enum[]) field.getType().getEnumConstants();
                    if(enums.length > 0) {
                        boolean isContain = false;
                        for(Enum e : enums) {
                            if(e.name().equals(cellValue)) {
                                isContain = true;
                                cellValue = e;
                                break;
                            }
                        }
                        if(isContain == false) {
                            /*cellValue = null;*/
                            EOMNotContainException e = new EOMNotContainException(row.getRowNum(), element.getIndex());
                            bodyException.addDetail(e);
                        }
                    }
                }

                // dropdown - dynamic
                if(element.getDropdown().equals(ColumnElement.INIT_DROPDOWN) == false) {
                    Object key = options.get(element.getDropdown()).get(cellValue);
                    if(key != null) {
                        cellValue = key;
                    } else {
                        EOMNotContainException e = new EOMNotContainException(row.getRowNum(), element.getIndex());
                        bodyException.addDetail(e);
                    }
                }

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

                try {
                    field.set(copyInstance, cellValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                // TODO exception check
            }
        });
        return instance;
    }

}
