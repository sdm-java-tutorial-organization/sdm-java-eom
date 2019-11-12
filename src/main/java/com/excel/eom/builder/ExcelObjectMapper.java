package com.excel.eom.builder;


import com.excel.eom.annotation.ExcelObject;
import com.excel.eom.annotation.UniqueKey;
import com.excel.eom.constant.StringConstant;
import com.excel.eom.exception.EOMBodyException;
import com.excel.eom.exception.EOMDevelopmentException;
import com.excel.eom.exception.EOMHeaderException;
import com.excel.eom.exception.body.EOMNotUniqueException;
import com.excel.eom.exception.body.EOMWrongDataTypeException;
import com.excel.eom.exception.development.EOMObjectException;
import com.excel.eom.exception.body.EOMNotContainException;
import com.excel.eom.exception.body.EOMNotNullException;
import com.excel.eom.exception.development.EOMWrongGroupException;
import com.excel.eom.exception.development.EOMWrongIndexException;
import com.excel.eom.exception.development.EOMWrongUniqueFieldException;
import com.excel.eom.model.FieldInfo;
import com.excel.eom.model.Dropdown;
import com.excel.eom.util.*;
import com.excel.eom.util.callback.FieldInfoCallback;
import com.excel.eom.util.callback.ExcelColumnInfoCallback;
import com.excel.eom.util.callback.ExcelObjectInfoCallback;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelObjectMapper {

    private static final Logger logger = LoggerFactory.getLogger(ExcelObjectMapper.class);

    private Class<?> clazz;
    private boolean isSXSSF = false;
    private Workbook book;
    private Sheet sheet;
    private DataValidationHelper dvHelper;
    private Map<Field, FieldInfo> fieldInfoMap = new HashMap<>();
    private Map<Field, CellStyle> fieldStyleMap = new HashMap<>();
    private Map<Field, DataValidationConstraint> fieldDropdownMap = new HashMap<>();
    private Map<String, Map<String, Object>> options = new HashMap<>();

    private ExcelObjectMapper() {

    }

    // ===================== public =====================

    /**
     * init
     */
    public static ExcelObjectMapper init() {
        return new ExcelObjectMapper();
    }

    /**
     * initModel
     *
     * @param clazz
     */
    public ExcelObjectMapper initModel(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    /**
     * initBook
     *
     * @param book
     */
    public ExcelObjectMapper initBook(Workbook book) {
        if(book.getClass().getSimpleName().equals(SXSSFWorkbook.class.getSimpleName())) {
            isSXSSF = true;
        }
        this.book = book;
        return this;
    }

    /**
     * initSheet
     *
     * @param sheet
     */
    public ExcelObjectMapper initSheet(Sheet sheet) {
        this.sheet = sheet;
        return this;
    }

    /**
     * initDropDowns
     *
     * @param dropdowns - array
     */
    public ExcelObjectMapper initDropDowns(Dropdown... dropdowns) {
        for (Dropdown dropdown : dropdowns) {
            this.options.put(dropdown.getKey(), dropdown.getOptions());
        }
        return this;
    }

    /**
     * buildObject (object -> sheet)
     *
     * @param objects
     */
    public <T> void buildObject(List<T> objects) throws EOMDevelopmentException, EOMBodyException {
        if (this.book != null &&
                this.sheet != null &&
                this.clazz != null &&
                objects != null &&
                objects.size() > 0) {
            initElements();
            validateAnnotation(fieldInfoMap);
            presetSheet();

            // [h]eader
            Row hRow = ExcelRowUtil.initRow(this.sheet, 0);
            setHeaderRow(hRow);

            // [b]ody
            EOMBodyException bodyException = new EOMBodyException();
            for (int i = 0; i < objects.size(); i++) {
                T object = objects.get(i);
                Row bRow = ExcelRowUtil.initRow(this.sheet, i + 1);
                setBodyRow(bRow, object, bodyException);
            }

            // [r]egion
            /*if (bodyException.countDetail() == 0) {

            }*/
            Map<Integer, List<Integer>> regionEndPointMapByGroup = checkRegion(objects);
            setRegion(regionEndPointMapByGroup);

            // [u]nique
            /*if (bodyException.countDetail() == 0) {

            }*/
            validateUniqueKey(objects, regionEndPointMapByGroup, bodyException);

            // [d]ropdown
            /*if (bodyException.countDetail() == 0) {

            }*/
            setDropDown(objects.size());

            if (bodyException.countDetail() > 0) {
                throw bodyException;
            }
        }
    }

    /**
     * buildSheet (sheet -> object)
     */
    public <T> List<T> buildSheet() throws EOMHeaderException, EOMBodyException {
        List<T> items = new ArrayList<>();

        if (this.sheet != null && this.clazz != null) {
            initElements();
            int sheetHeight = ExcelSheetUtil.getSheetHeight(this.sheet);
            if (sheetHeight > 1) {

                // [h]eader - haederException
                Row hRow = ExcelRowUtil.getRow(this.sheet, 0);
                checkHeader(hRow);

                // [b]ody - bodyException
                EOMBodyException bodyException = new EOMBodyException(/*items*/);
                Map<Field, Object> areaValues = new HashMap<>(); // region first cell value storage
                for (int i = 1; i < sheetHeight; i++) {
                    // empty valid
                    if (ExcelRowUtil.isEmptyRow(this.sheet.getRow(i)) == false) {
                        items.add((T) getObjectByRow(this.sheet.getRow(i), areaValues, bodyException));
                    }
                }

                // [u]nique - bodyException
                Map<Integer, List<Integer>> regionEndPointMapByGroup = checkRegion(items);
                validateUniqueKey(items, regionEndPointMapByGroup, bodyException);

                if (bodyException.countDetail() > 0) {
                    throw bodyException;
                }
            }
        }
        return items;
    }

    // ===================== private =====================

    /**
     * initElements
     */
    private void initElements() throws EOMDevelopmentException {
        if (this.clazz != null) {
            try {
                Object instance = this.clazz.newInstance();
            } catch (InstantiationException e) {
                Map<String, String> args = EOMObjectException.getArguments(clazz.getSimpleName());
                throw new EOMObjectException(args);
            } catch (IllegalAccessException e) {
                Map<String, String> args = EOMObjectException.getArguments(clazz.getSimpleName());
                throw new EOMObjectException(args);
            }

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
                    fieldInfoMap.put(field, new FieldInfo(name, index, group, dropdown, nullable));
                }
            });
        }
    }

    /**
     * validateGroup - 그룹에서 가져야할 규칙이 올바르게 적용되어 있는지 확인
     */
    private void validateAnnotation(Map<Field, FieldInfo> fieldInfoMap) throws EOMDevelopmentException {
        List<FieldInfo> list = new ArrayList<>(fieldInfoMap.values());
        list = list.stream().sorted(new Comparator<FieldInfo>() {
            @Override
            public int compare(FieldInfo o1, FieldInfo o2) {
                return Integer.compare(o1.getIndex(), o2.getIndex());
            }
        }).collect(Collectors.toList());

        Integer preIndex = null;
        Integer preGroup = null;
        Iterator<FieldInfo> iterator = list.iterator();
        while (iterator.hasNext()) {
            FieldInfo columnElement = iterator.next();

            if (preIndex == null) {
                preIndex = columnElement.getIndex();
            } else {
                if (preIndex + 1 != columnElement.getIndex()) {
                    Map<String, String> args = EOMWrongIndexException.getArguments(clazz.getSimpleName(), columnElement.getIndex());
                    throw new EOMWrongIndexException(args);
                }
                preIndex = columnElement.getIndex();
            }

            if (preGroup == null) {
                preGroup = columnElement.getGroup();
                columnElement.setNullable(false);
            } else {
                if (preGroup > columnElement.getGroup()) {
                    preGroup = columnElement.getGroup();
                    /*columnElement.setNullable(false);*/
                } else if (preGroup < columnElement.getGroup()) {
                    Map<String, String> args = EOMWrongGroupException.getArguments(clazz.getSimpleName(), columnElement.getGroup());
                    throw new EOMWrongGroupException(args);
                }
            }
        }
    }

    /**
     * presetSheet ( object -> sheet )
     */
    private void presetSheet() {
        if (this.clazz != null && this.sheet != null) {
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
                    if (field.getType().isEnum()) {
                        if (dvHelper == null && isSXSSF == false) {
                            XSSFSheet xssfSheet = (XSSFSheet) sheet;
                            dvHelper = new XSSFDataValidationHelper(xssfSheet);
                        }

                        Object[] enums = field.getType().getEnumConstants();
                        List<String> items = new ArrayList<>();
                        for (Object e : enums) {
                            items.add(e.toString());
                        }
                        fieldDropdownMap.put(field, ExcelSheetUtil.getDropdown(dvHelper, items.toArray(new String[]{})));
                    }

                    // init dynamic dropdown (sheet)
                    if (dropdown.equals("") == false && isSXSSF == false) {
                        if (dvHelper == null) {
                            XSSFSheet xssfSheet = (XSSFSheet) sheet;
                            dvHelper = new XSSFDataValidationHelper(xssfSheet);
                        }
                        // TODO null check and exception
                        Map<String, Object> option = options.get(dropdown);
                        List<String> items = new ArrayList(option.keySet());
                        fieldDropdownMap.put(field, ExcelSheetUtil.getDropdown(dvHelper, items.toArray(new String[]{})));
                    }

                    // init style (book)
                    if (book != null && isSXSSF == false) {
                        XSSFWorkbook xssfWorkbook = (XSSFWorkbook) book;
                        XSSFCellStyle cellStyle = ExcelCellUtil.getCellStyle(xssfWorkbook);
                        ExcelCellUtil.setCellColor(cellStyle, cellColor);
                        ExcelCellUtil.setCellBorder(cellStyle, borderStyle, borderColor);
                        ExcelCellUtil.setAlignment(cellStyle, alignment);
                        fieldStyleMap.put(field, cellStyle);
                    }
                }
            });
        }
    }

    /**
     * setHeaderRow
     */
    private void setHeaderRow(final Row row) {
        final Workbook book = this.book;
        final Map<Field, FieldInfo> fieldInfoMap = this.fieldInfoMap;
        ReflectionUtil.getClassInfo(this.clazz, new ExcelObjectInfoCallback() {
            @Override
            public void getClassInfo(String name,
                                     IndexedColors cellColor,
                                     BorderStyle borderStyle,
                                     IndexedColors borderColor,
                                     int fixedRowCount,
                                     int fixedColumnCount) {
                if(isSXSSF == false) {
                    XSSFWorkbook xssfWorkbook = (XSSFWorkbook) book;
                    XSSFCellStyle cellStyle = ExcelCellUtil.getCellStyle(xssfWorkbook);
                    XSSFFont font = ExcelCellUtil.getFont(xssfWorkbook);

                    // style - static
                    font.setBold(true);
                    ExcelSheetUtil.createFreezePane(sheet, fixedColumnCount, fixedRowCount);
                    ExcelCellUtil.setAlignment(cellStyle, CellStyle.ALIGN_CENTER);
                    ExcelCellUtil.setFont(cellStyle, font);

                    // style - dynamic
                    ExcelCellUtil.setCellColor(cellStyle, cellColor);
                    ExcelCellUtil.setCellBorder(cellStyle, borderStyle, borderColor);

                    FieldInfoUtil.getEachFieldInfo(fieldInfoMap, new FieldInfoCallback() {
                        @Override
                        public void getFieldInfo(Field field, FieldInfo fieldInfo) {
                            Cell cell = ExcelCellUtil.getCell(row, fieldInfo.getIndex());
                            ExcelCellUtil.setCellStyle(cell, cellStyle);
                        }
                    });
                }

                FieldInfoUtil.getEachFieldInfo(fieldInfoMap, new FieldInfoCallback() {
                    @Override
                    public void getFieldInfo(Field field, FieldInfo fieldInfo) {
                        Cell cell = ExcelCellUtil.getCell(row, fieldInfo.getIndex());
                        ExcelCellUtil.setCellValue(cell, fieldInfo.getName());
                    }
                });
            }
        });
    }

    /**
     * setBodyRow(object->sheet)
     *
     * @param row
     * @param object
     * @param bodyException
     */
    private <T> void setBodyRow(Row row, T object, EOMBodyException bodyException) {
        FieldInfoUtil.getEachFieldInfo(this.fieldInfoMap, new FieldInfoCallback() {
            @Override
            public void getFieldInfo(Field field, FieldInfo fieldInfo) {
                Cell cell = ExcelCellUtil.getCell(row, fieldInfo.getIndex());
                Object cellValue = null;
                String cellType = null;

                try {
                    cellValue = field.get(object);
                } catch (IllegalAccessException e) {
                    // * DOESN'T OCCUR (because of initElement)
                }

                // nullable
                if (fieldInfo.getNullable() == false) {
                    if (cellValue == null || cellValue.equals("")) {
                        Map<String, String> args = EOMNotNullException.getArguments(row.getRowNum(), fieldInfo.getIndex());
                        EOMNotNullException e = new EOMNotNullException(args);
                        bodyException.addDetail(e);
                    }
                }

                // dropdown - satic
                if (field.getType().isEnum()) {
                    if (cellValue == null) {
                        Map<String, String> args = EOMNotContainException.getArguments(cellValue, row.getRowNum(), fieldInfo.getIndex());
                        EOMNotContainException e = new EOMNotContainException(args);
                        bodyException.addDetail(e);
                    }
                }

                // dropdown - dynamic
                if (fieldInfo.getDropdown().equals(FieldInfo.INIT_DROPDOWN) == false) {
                    Iterator iterator = options.get(fieldInfo.getDropdown()).entrySet().iterator();
                    boolean isContain = false;
                    while (iterator.hasNext()) {
                        Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                        Object value = entry.getValue();
                        if (cellValue.equals(value)) {
                            cellValue = entry.getKey();
                            cellType = entry.getKey().getClass().getSimpleName();
                            isContain = true;
                        }
                    }
                    if (isContain == false) {
                        Map<String, String> args = EOMNotContainException.getArguments(cellValue, row.getRowNum(), fieldInfo.getIndex());
                        EOMNotContainException e = new EOMNotContainException(args);
                        bodyException.addDetail(e);
                    }
                }

                CellStyle style = fieldStyleMap.get(field);
                ExcelCellUtil.setCellValue(cell, cellValue, (cellType != null ? cellType : field));
                ExcelCellUtil.setCellStyle(cell, style);
            }
        });
    }

    /**
     * checkRegion - pre region validation (EndPoint is after the last point)
     *
     *
     * @param objects
     * @return Map[GroupLevel(int), List[EndPoint(int)]]
     */
    private <T> Map<Integer, List<Integer>> checkRegion(List<T> objects) {
        Map<Integer, List<Integer>> regionEndPointMapByGroup = new HashMap<>(); /* Map<Group, List<EndPoint>> */

        // extract group column & sort
        Map<Field, FieldInfo> regionFieldInfo = FieldInfoUtil.extractFieldInfoByGroup(this.fieldInfoMap);
        List<Map.Entry<Field, FieldInfo>> regionFieldInfoList = FieldInfoUtil.getElementListWithSort(regionFieldInfo);

        /**
         * [FLOW]
         * for field(by group):
         *  get FirstGroup
         *  for objects:
         *    get EndPoint
         * return List[EndPoint]
         * */
        List nowEndPoints = new ArrayList<>(), preEndPoints = new ArrayList<>();
        Iterator<Map.Entry<Field, FieldInfo>> iterator = regionFieldInfoList.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Field, FieldInfo> entry = iterator.next();
            Field field = entry.getKey();
            FieldInfo fieldInfo = entry.getValue();

            boolean isGroup, isFirstGroup = false;
            if (fieldInfo.getGroup() > 0) {
                // [1] is Group
                if (regionEndPointMapByGroup.get(fieldInfo.getGroup()) == null) {
                    // [1.1] is FirstGroup
                    isFirstGroup = true;
                    preEndPoints = nowEndPoints;
                    nowEndPoints = new ArrayList<>();
                    regionEndPointMapByGroup.put(fieldInfo.getGroup(), nowEndPoints);
                } else {
                    // [1.2] non FirstGroup
                    isFirstGroup = false;
                }
                isGroup = true;
            } else {
                // [1] non Group
                isGroup = false;
            }

            // first group validation
            // already done (in object -> sheet or sheet -> object)
            /*if (isFirstGroup) {
                for (int i = 0; i < objects.size(); i++) {
                    T object = objects.get(i);
                    Object cellValue = ReflectionUtil.getFieldValue(field, object);
                    // NullPointerException - can't exist null in firstGroup
                    if (cellValue == null) { }
                }
            }*/

            Object preValue = ReflectionUtil.getFieldValue(field, objects.get(0));
            for (int i = 0; i < objects.size(); i++) {
                T object = objects.get(i);
                Object cellValue = ReflectionUtil.getFieldValue(field, object);
                if (isGroup) {
                    // group
                    if (isFirstGroup) {
                        if (preValue.equals(cellValue) == false) {
                            preValue = cellValue;
                            nowEndPoints.add((i + 1) - 1); // * pre cell merge (-1)
                        } else {
                            if (preEndPoints != null) {
                                if (preEndPoints.indexOf((i + 1) - 1) > -1) {
                                    nowEndPoints.add((i + 1) - 1); // * pre cell merge (-1)
                                }
                            }
                        }
                    } else {
                        // TODO exception check - must same upper cell
                    }
                }
            }

            // last EndPoint
            if (isFirstGroup) {
                nowEndPoints.add((objects.size() + 1) - 1); // * pre cell merge (-1)
            }
        }
        return regionEndPointMapByGroup;
    }

    /**
     * setRegion
     *
     * @param regionEndPointMapByGroup
     */
    private void setRegion(Map<Integer, List<Integer>> regionEndPointMapByGroup) {
        Iterator iterator = regionEndPointMapByGroup.entrySet().stream().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, List<Integer>> entry = (Map.Entry<Integer, List<Integer>>) iterator.next();
            Integer group = entry.getKey();
            List<Integer> endPoints = entry.getValue();
            FieldInfoUtil.getEachFieldInfo(fieldInfoMap, new FieldInfoCallback() {
                @Override
                public void getFieldInfo(Field field, FieldInfo fieldInfo) {
                    if (fieldInfo.getGroup() == group) {
                        int startPoint = 1;
                        for (int endPoint : endPoints) {
                            if (endPoint - startPoint > 0) {
                                ExcelSheetUtil.addRegion(sheet, startPoint, endPoint, fieldInfo.getIndex(), fieldInfo.getIndex());
                            }
                            startPoint = ++endPoint;
                        }
                    }
                }
            });
        }
    }

    /**
     * setDropDown
     *
     * @param size
     */
    private void setDropDown(int size) {
        Map<Field, FieldInfo> regionFieldInfo = FieldInfoUtil.extractFieldInfoByDropdown(this.fieldInfoMap);
        List<Map.Entry<Field, FieldInfo>> regionFieldInfoList = FieldInfoUtil.getElementListWithSort(regionFieldInfo);
        Iterator<Map.Entry<Field, FieldInfo>> iterator = regionFieldInfoList.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Field, FieldInfo> entry = iterator.next();
            Field field = entry.getKey();
            FieldInfo fieldInfo = entry.getValue();
            if (field.getType().isEnum()) {
                // [1] static (enum)
                for (int i = 0; i < size; i++) {
                    CellRangeAddressList region = ExcelSheetUtil.getRegion(
                            (i + 1), (i + 1), fieldInfo.getIndex(), fieldInfo.getIndex());
                    ExcelSheetUtil.setDropdown(
                            this.sheet,
                            this.dvHelper,
                            this.fieldDropdownMap.get(field),
                            region);
                }
            } else {
                // [2] dynamic
                for (int i = 0; i < size; i++) {
                    CellRangeAddressList region = ExcelSheetUtil.getRegion(
                            (i + 1), (i + 1), fieldInfo.getIndex(), fieldInfo.getIndex());
                    ExcelSheetUtil.setDropdown(
                            this.sheet,
                            this.dvHelper,
                            this.fieldDropdownMap.get(field),
                            region);
                }
            }
        }
    }

    /**
     * checkHeader
     */
    private void checkHeader(Row row) throws EOMHeaderException {
        final Map<Field, FieldInfo> fieldInfoMap = this.fieldInfoMap;
        FieldInfoUtil.getEachFieldInfo(fieldInfoMap, new FieldInfoCallback() {
            @Override
            public void getFieldInfo(Field field, FieldInfo fieldInfo) {
                Cell cell = ExcelCellUtil.getCell(row, fieldInfo.getIndex());
                Object cellValue = ExcelCellUtil.getCellValue(cell);
                if (fieldInfo.getName().equals(cellValue) == false) {
                    // * THROW WRONG_HEADER_EXCEPTION
                    throw new EOMHeaderException(null);
                }
            }
        });
    }

    /**
     * getObjectByRow (sheet -> object)
     *
     * @param row
     * @param areaValues
     * @param bodyException
     */
    private Object getObjectByRow(Row row,
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

        final Sheet sheet = this.sheet;
        final Object copyInstance = instance;
        int rowIdx = ExcelRowUtil.getRowNum(row);
        FieldInfoUtil.getEachFieldInfo(this.fieldInfoMap, new FieldInfoCallback() {
            @Override
            public void getFieldInfo(Field field, FieldInfo fieldInfo) {
                int colIdx = fieldInfo.getIndex();
                Cell cell = row.getCell(colIdx);
                Object cellValue = ExcelCellUtil.getCellValue(cell);

                // region value management
                if (fieldInfo.getGroup() > 0) {
                    CellRangeAddress region = ExcelSheetUtil.getMergedRegion(sheet, rowIdx, colIdx);
                    if (region != null) {
                        if (region.getFirstRow() == rowIdx) {
                            areaValues.put(field, cellValue);
                        } else {
                            cellValue = areaValues.get(field);
                        }
                    }
                }

                // nullable
                if (fieldInfo.getNullable() == false) {
                    if (cellValue == null || cellValue.equals("")) {
                        Map<String, String> args = EOMNotNullException.getArguments(row.getRowNum(), fieldInfo.getIndex());
                        EOMNotNullException e = new EOMNotNullException(args);
                        bodyException.addDetail(e);
                        return;
                    }
                }

                // dropdown - static
                if (field.getType().isEnum()) {
                    Enum[] enums = (Enum[]) field.getType().getEnumConstants();
                    if (enums.length > 0) {
                        boolean isContain = false;
                        for (Enum e : enums) {
                            if (e.name().equals(cellValue)) {
                                isContain = true;
                                cellValue = e;
                                break;
                            }
                        }
                        if (isContain == false) {
                            /*cellValue = null;*/
                            if (fieldInfo.getNullable() && (cellValue == null || cellValue.equals(""))) {
                                cellValue = null;
                            } else {
                                Map<String, String> args = EOMNotContainException.getArguments(cellValue, row.getRowNum(), fieldInfo.getIndex());
                                EOMNotContainException e = new EOMNotContainException(args);
                                bodyException.addDetail(e);
                                return;
                            }
                        }
                    }
                }

                // dropdown - dynamic
                if (
                        fieldInfo.getDropdown().equals(FieldInfo.INIT_DROPDOWN) == false
                                &&
                                options.get(fieldInfo.getDropdown()) != null
                ) {
                    Object key = options.get(fieldInfo.getDropdown()).get(cellValue);
                    if (key != null) {
                        cellValue = key;
                    } else {
                        if (fieldInfo.getNullable()) {
                            cellValue = null;
                        } else {
                            Map<String, String> args = EOMNotContainException.getArguments(cellValue, row.getRowNum(), fieldInfo.getIndex());
                            EOMNotContainException e = new EOMNotContainException(args);
                            bodyException.addDetail(e);
                            return;
                        }
                    }
                }

                try {
                    String type = field.getType().getSimpleName();
                    switch (type) {
                        // [1] string -> number
                        case StringConstant.INTEGER:
                            if (cellValue != null) {
                                if (cellValue.getClass().getSimpleName().equals(StringConstant.INTEGER) == false) {
                                    cellValue = Integer.parseInt((String) cellValue);
                                }
                            }
                            break;
                        // [2] number -> string
                        case StringConstant.STRING:
                            if (cellValue != null) {
                                if (cellValue.getClass().getSimpleName().equals(StringConstant.INTEGER) == false
                                        ||
                                        cellValue.getClass().getSimpleName().equals(StringConstant.DOUBLE) == false
                                ) {
                                    cellValue = cellValue + "";
                                }
                            } else {
                                cellValue = "";
                            }
                            break;
                    }
                    field.set(copyInstance, cellValue);
                } catch (IllegalAccessException e) {
                    Map<String, String> args = EOMWrongDataTypeException.getArguments(cellValue, row.getRowNum(), fieldInfo.getIndex());
                    EOMWrongDataTypeException wrongDataTypeException = new EOMWrongDataTypeException(args);
                    bodyException.addDetail(wrongDataTypeException);
                } catch (IllegalArgumentException e) {
                    Map<String, String> args = EOMWrongDataTypeException.getArguments(cellValue, row.getRowNum(), fieldInfo.getIndex());
                    EOMWrongDataTypeException wrongDataTypeException = new EOMWrongDataTypeException(args);
                    bodyException.addDetail(wrongDataTypeException);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return instance;
    }

    /**
     * validateUniqueKey
     *
     * @param objects
     * @param bodyException
     */
    private <T> void validateUniqueKey(List<T> objects,
                                       Map<Integer, List<Integer>> regionEndPointMapByGroup,
                                       EOMBodyException bodyException) throws EOMDevelopmentException {
        List<List<String>> uniqueKeys = UniqueKeyUtil.getUniqueValues(clazz);
        if (uniqueKeys.size() == 0) {
            return;
        }

        // field validate
        Field[] fields = clazz.getDeclaredFields();
        boolean validateResult = UniqueKeyUtil.validateField(fields, uniqueKeys);
        if (validateResult == false) {
            Map<String, String> args = EOMWrongUniqueFieldException.getArguments(UniqueKeyUtil.getWrongField(fields, uniqueKeys));
            throw new EOMWrongUniqueFieldException(args);
        }

        // init <>
        Map<List<Field>, Set<String>> uniqueKeyStorage = new HashMap<>();
        Map<List<Field>, Integer> uniqueKeyGroupLevelStorage = new HashMap<>();
        for (List<String> uniqueKey : uniqueKeys) {
            List<Field> key = new ArrayList<>();
            List<Integer> groups = new ArrayList<>();
            for (String fieldElement : uniqueKey) {
                FieldInfoUtil.getEachFieldInfo(this.fieldInfoMap, new FieldInfoCallback() {
                    @Override
                    public void getFieldInfo(Field field, FieldInfo fieldInfo) {
                        if (field.getName().equals(fieldElement)) {
                            key.add(field);
                            groups.add(fieldInfo.getGroup());
                        }
                    }
                });
            }
            uniqueKeyStorage.put(key, new HashSet<>());
            uniqueKeyGroupLevelStorage.put(key, groups.stream().min(Integer::min).get());
        }

        // unique validate
        for (int i = 0; i < objects.size(); i++) {
            T object = objects.get(i);
            Iterator<Map.Entry<List<Field>, Set<String>>> iterator = uniqueKeyStorage.entrySet().stream().iterator();
            while (iterator.hasNext()) {
                Map.Entry<List<Field>, Set<String>> entry = iterator.next();
                List<Field> uniqueKeyByFields = entry.getKey();
                Set<String> uniqueValueStorage = entry.getValue();

                int uniqueKeyGroupLevel = uniqueKeyGroupLevelStorage.get(uniqueKeyByFields);
                List<Integer> groupEndPoint = regionEndPointMapByGroup.get(uniqueKeyGroupLevel);

                // isLowerGroup ? OR isFirstGroup ?
                if(uniqueKeyGroupLevel == 0 || groupEndPoint.indexOf(i) > 0) {
                    Object result = uniqueKeyByFields.stream()
                            .map(field -> {
                                try {
                                    Object fieldObject = field.get(object);
                                    if(fieldObject != null) {
                                        return fieldObject;
                                    } else {
                                        return "";
                                    }
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                    return "";
                                }
                            })
                            .reduce((a, b) -> a + "&&" + b)
                            .get();
                    if (uniqueValueStorage.contains(result + "")) {
                        // TODO need to column guide
                        Map<String, String> args = EOMNotUniqueException.getArguments(result, i);
                        EOMNotUniqueException e = new EOMNotUniqueException(args);
                        bodyException.addDetail(e);
                    } else {
                        uniqueValueStorage.add(result + "");
                    }
                }

            }
        }
    }

}
