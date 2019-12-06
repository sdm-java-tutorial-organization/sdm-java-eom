package com.excel.eom.builder;

import com.excel.eom.constant.ExcelConstant;
import com.excel.eom.constant.StringConstant;
import com.excel.eom.exception.EOMBodyException;
import com.excel.eom.exception.EOMDevelopmentException;
import com.excel.eom.exception.EOMHeaderException;
import com.excel.eom.exception.body.EOMNotUniqueException;
import com.excel.eom.exception.body.EOMWrongDataTypeException;
import com.excel.eom.exception.development.*;
import com.excel.eom.exception.body.EOMNotContainException;
import com.excel.eom.exception.body.EOMNotNullException;
import com.excel.eom.model.FieldInfo;
import com.excel.eom.model.Dropdown;
import com.excel.eom.model.GroupRegionList;
import com.excel.eom.util.*;
import com.excel.eom.util.callback.ExcelColumnInfoCallback;
import com.excel.eom.util.callback.FieldInfoCallback;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelObjectMapper {

    private Class<?> clazz;
    private Workbook book;
    private Sheet sheet;
    private String sheetName;
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
     *
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
        this.sheetName = sheet.getSheetName();
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
     * List[Object] -> Sheet 빌드합니다.
     *
     * @param objects
     */
    public <T> void buildObject(List<T> objects) throws EOMDevelopmentException, EOMBodyException {
        if (this.book != null && this.sheet != null && this.clazz != null && objects != null) {

            validateAnnotation(fieldInfoMap);
            initFieldInfo(fieldInfoMap);
            presetSheet();

            // Header
            Row hRow = ExcelRowUtil.initRow(this.sheet, 0);
            setHeaderRow(hRow);

            if(objects.size() > 0) {

                // object of list validation
                T t = objects.get(0);
                if(ReflectionUtil.isSameClass(t.getClass(), this.clazz) == false) {
                    Map<String, String> args = EOMWrongListException.getArguments(t.getClass().getSimpleName(), this.clazz.getSimpleName());
                    throw new EOMWrongListException(sheetName, args);
                }

                // Body
                EOMBodyException bodyException = new EOMBodyException(sheetName);
                for (int i = 0; i < objects.size(); i++) {
                    T object = objects.get(i);
                    Row bRow = ExcelRowUtil.initRow(this.sheet, i + 1);
                    setBodyRow(bRow, object, bodyException);
                }

                // Region
                GroupRegionList groupRegionList = checkRegion(objects);
                setRegion(groupRegionList);

                // Unique
                validateUniqueKey(objects, groupRegionList, bodyException);

                // Dropdown
                setDropDown(objects.size());

                if (bodyException.countDetail() > 0) {
                    throw bodyException;
                }
            }
        }
    }

    /**
     * buildSheet (sheet -> object)
     *
     */
    public <T> List<T> buildSheet() throws EOMHeaderException, EOMBodyException {
        List<T> items = new ArrayList<>();

        if (this.sheet != null && this.clazz != null) {
            initFieldInfo(fieldInfoMap);
            int sheetHeight = ExcelSheetUtil.getSheetHeight(this.sheet);
            if (sheetHeight > 1) {

                // [h]eader - haederException
                Row hRow = ExcelRowUtil.getRow(this.sheet, 0);
                checkHeader(hRow);

                // [b]ody - bodyException
                EOMBodyException bodyException = new EOMBodyException(sheetName);
                Map<Field, Object> areaValues = new HashMap<>(); // region first cell value storage
                for (int i = 1; i < sheetHeight; i++) {
                    // empty valid
                    if (ExcelRowUtil.isEmptyRow(this.sheet.getRow(i)) == false) {
                        items.add((T) getObjectByRow(this.sheet.getRow(i), areaValues, bodyException));
                    }
                }

                // [u]nique - bodyException
                GroupRegionList groupRegionList = checkRegion(items);
                validateUniqueKey(items, groupRegionList, bodyException);

                if (bodyException.countDetail() > 0) {
                    throw bodyException;
                }
            }
        }
        return items;
    }

    // ===================== private =====================

    /**
     * FieldInfo 초기화함수 (ExcelObject 내에 정의된 Field 값들을 FieldInfo 모델로 이동)
     *
     */
    private void initFieldInfo(Map<Field, FieldInfo> fieldInfoMap) throws EOMDevelopmentException {
        if (this.clazz != null) {
            try {
                Object instance = this.clazz.newInstance();
            } catch (InstantiationException e) {
                Map<String, String> args = EOMObjectException.getArguments(clazz.getSimpleName(), e.getMessage());
                throw new EOMObjectException(sheetName, args);
            } catch (IllegalAccessException e) {
                Map<String, String> args = EOMObjectException.getArguments(clazz.getSimpleName(), e.getMessage());
                throw new EOMObjectException(sheetName, args);
            }

            ReflectionUtil.getFieldInfo(this.clazz, new ExcelColumnInfoCallback() {
                @Override
                public void getFieldInfo(// == info ==
                                         Field field, String name, Integer index, Integer group, String dropdown, Boolean nullable,
                                         // == style ==
                                         Integer width, Short alignment, IndexedColors cellColor, short borderStyle, IndexedColors borderColor) {
                    fieldInfoMap.put(field, new FieldInfo(name, index, group, dropdown, nullable, width, alignment, cellColor, borderStyle, borderColor));
                }
            });
        }
    }

    /**
     * 순서(index) & 그룹(group) 에서 가져야할 규칙이 올바르게 적용되어 있는지 확인
     *
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
                    throw new EOMWrongIndexException(sheetName, args);
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
                    throw new EOMWrongGroupException(sheetName, args);
                }
            }
        }
    }

    /**
     * presetSheet ( object -> sheet )
     * 시트를 설정하기 위한 값을 미리 정의하는 함수 (fieldDropdownMap, fieldStyleMap)
     *
     */
    private void presetSheet() throws EOMDevelopmentException {
        FieldInfoUtil.getEachFieldInfo(fieldInfoMap, new FieldInfoCallback() {
            @Override
            public void getFieldInfo(Field field, FieldInfo fieldInfo) {

                // init width (sheet)
                sheet.setColumnWidth(fieldInfo.getIndex(), fieldInfo.getStyleInfo().getWidth() * ExcelConstant.CELL_WIDTH_DEFAULT);

                // init enum(static) dropdown (sheet)
                if (field.getType().isEnum()) {
                    if (dvHelper == null) {
                        dvHelper = sheet.getDataValidationHelper();
                    }
                    String[] enumArray = DropdownUtil.getEnumToArray(field);
                    fieldDropdownMap.put(field, ExcelSheetUtil.getDropdown(dvHelper, enumArray));
                }

                // init dynamic dropdown (sheet)
                if (fieldInfo.getDropdown().equals("") == false) {
                    if (dvHelper == null) {
                        dvHelper = sheet.getDataValidationHelper();
                    }
                    Map<String, Object> option = options.get(fieldInfo.getDropdown());
                    if(option == null) {
                        Map<String, String> args = EOMNotFoundDropdownKeyException.getArguments(field.getName(), fieldInfo.getDropdown());
                        throw new EOMNotFoundDropdownKeyException(sheetName, args);
                    }
                    List<String> items = new ArrayList(option.keySet());
                    fieldDropdownMap.put(field, ExcelSheetUtil.getDropdown(dvHelper, items.toArray(new String[]{})));
                }

                // init style (book)
                if (book != null) {
                    CellStyle cellStyle = ExcelCellUtil.getCellStyle(book);
                    ExcelCellUtil.setCellColor(cellStyle, fieldInfo.getStyleInfo().getCellColor());
                    ExcelCellUtil.setCellBorder(cellStyle, fieldInfo.getStyleInfo().getBorderStyle(), fieldInfo.getStyleInfo().getBorderColor());
                    ExcelCellUtil.setAlignment(cellStyle, fieldInfo.getStyleInfo().getAlignment());
                    fieldStyleMap.put(field, cellStyle);
                }
            }
        });
    }

    /**
     * setHeaderRow
     *
     */
    private void setHeaderRow(final Row row) {
        final Workbook book = this.book;
        CellStyle cellStyle = HeaderUtil.getHeaderCellStyle(book, sheet, clazz);
        FieldInfoUtil.getEachFieldInfo(fieldInfoMap, new FieldInfoCallback() {
            @Override
            public void getFieldInfo(Field field, FieldInfo fieldInfo) {
                Cell cell = ExcelCellUtil.getCell(row, fieldInfo.getIndex());
                ExcelCellUtil.setCellStyle(cell, cellStyle);
                ExcelCellUtil.setCellValue(cell, fieldInfo.getName());
            }
        });
    }

    /**
     * object -> sheet
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
                    // * DOESN'T OCCUR (because of initFieldInfo)
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
     * 그룹별로 영역의 종료점(EndPoint)만 모아서 반환
     *
     * @param objects
     * @return List[GroupRegion]
     */
    private <T> GroupRegionList checkRegion(List<T> objects) {
        GroupRegionList groupRegions = new GroupRegionList();
        /*Map<Integer, List<Integer>> regionEndPointMapByGroup = new HashMap<>(); *//* Map<Group, List<EndPoint>> */

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
                if(groupRegions.isContainGroup(fieldInfo.getGroup()) == false) {
                    // [1.1] is FirstGroup
                    isFirstGroup = true;
                    preEndPoints = nowEndPoints;
                    nowEndPoints = new ArrayList<>();
                    groupRegions.addGroupRegion(fieldInfo.getGroup(), nowEndPoints);
                } else {
                    // [1.2] non FirstGroup
                    isFirstGroup = false;
                }
                isGroup = true;
            } else {
                // [1] non Group
                isGroup = false;
            }

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
        return groupRegions;
    }

    /**
     * Region 설정
     *
     * @param groupRegionList
     */
    private void setRegion(GroupRegionList groupRegionList) {
        FieldInfoUtil.getEachFieldInfo(fieldInfoMap, new FieldInfoCallback() {
            @Override
            public void getFieldInfo(Field field, FieldInfo fieldInfo) {
                List<Integer> endPoints = groupRegionList.findGroupRegionEndPoint(fieldInfo.getGroup());
                if (endPoints.size() > 0) {
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

    /**
     * setDropDown
     *
     * @param objectSize
     */
    private void setDropDown(int objectSize) {
        Map<Field, FieldInfo> regionFieldInfo = FieldInfoUtil.extractFieldInfoByDropdown(this.fieldInfoMap);
        List<Map.Entry<Field, FieldInfo>> regionFieldInfoList = FieldInfoUtil.getElementListWithSort(regionFieldInfo);
        Iterator<Map.Entry<Field, FieldInfo>> iterator = regionFieldInfoList.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Field, FieldInfo> entry = iterator.next();
            Field field = entry.getKey();
            FieldInfo fieldInfo = entry.getValue();
            if (field.getType().isEnum()) {
                // [1] static (enum)
                for (int i = 0; i < objectSize; i++) {
                    CellRangeAddressList region = ExcelSheetUtil.getRegion((i + 1), (i + 1), fieldInfo.getIndex(), fieldInfo.getIndex());
                    ExcelSheetUtil.setDropdown(this.sheet, this.dvHelper, this.fieldDropdownMap.get(field), region);
                }
            } else {
                // [2] dynamic
                for (int i = 0; i < objectSize; i++) {
                    CellRangeAddressList region = ExcelSheetUtil.getRegion((i + 1), (i + 1), fieldInfo.getIndex(), fieldInfo.getIndex());
                    ExcelSheetUtil.setDropdown(this.sheet, this.dvHelper, this.fieldDropdownMap.get(field), region);
                }
            }
        }
    }

    /**
     * check header name
     *
     * @throws EOMHeaderException
     */
    private void checkHeader(Row row) throws EOMHeaderException {
        final Map<Field, FieldInfo> fieldInfoMap = this.fieldInfoMap;
        FieldInfoUtil.getEachFieldInfo(fieldInfoMap, new FieldInfoCallback() {
            @Override
            public void getFieldInfo(Field field, FieldInfo fieldInfo) {
                Cell cell = ExcelCellUtil.getCell(row, fieldInfo.getIndex());
                Object cellValue = ExcelCellUtil.getCellValue(cell);
                if (fieldInfo.getName().equals(cellValue) == false)
                    throw new EOMHeaderException(sheetName, null); // * THROW WRONG_HEADER_EXCEPTION
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
            // * DOESN'T OCCUR (initFieldInfo)
        } catch (IllegalAccessException e) {
            // * DOESN'T OCCUR (initFieldInfo)
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
                                return;
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
                            return;
                        } else {
                            Map<String, String> args = EOMNotContainException.getArguments(cellValue, row.getRowNum(), fieldInfo.getIndex());
                            EOMNotContainException e = new EOMNotContainException(args);
                            bodyException.addDetail(e);
                            return;
                        }
                    }
                }

                try {
                    String fieldType = field.getType().getSimpleName();
                    String cellType = cellValue.getClass().getSimpleName();
                    switch (cellType) {
                        // [1] number(cell) -> string
                        case StringConstant.INTEGER:
                        case StringConstant.INTEGER_PRIMITIVE:
                        case StringConstant.DOUBLE:
                        case StringConstant.DOUBLE_PRIMITIVE:
                        case StringConstant.LONG:
                        case StringConstant.LONG_PRIMITIVE:
                            if (cellValue != null) {
                                switch (fieldType) {
                                    // Integer
                                    case StringConstant.INTEGER:
                                    case StringConstant.INTEGER_PRIMITIVE:
                                        break;
                                    // Double
                                    case StringConstant.DOUBLE:
                                    case StringConstant.DOUBLE_PRIMITIVE:
                                        cellValue = Double.valueOf((Integer) cellValue); // Integer -> Double
                                        break;
                                    // Long
                                    case StringConstant.LONG:
                                    case StringConstant.LONG_PRIMITIVE:
                                        cellValue = Long.valueOf((Integer) cellValue); // Integer -> Long
                                        break;
                                    // String
                                    case StringConstant.STRING:
                                        if(cellValue == null) {
                                            cellValue = "";
                                        } else {
                                            cellValue = cellValue + "";
                                        }
                                        break;
                                }
                            } else {
                                cellValue = 0;
                            }
                            break;
                        // [2] string(cell) -> number
                        case StringConstant.STRING:
                            if (cellValue != null) {
                                switch (fieldType) {
                                    // Integer
                                    case StringConstant.INTEGER:
                                    case StringConstant.INTEGER_PRIMITIVE:
                                        cellValue = Integer.parseInt((String) cellValue); // String -> Integer
                                        break;
                                    // Double
                                    case StringConstant.DOUBLE:
                                    case StringConstant.DOUBLE_PRIMITIVE:
                                        cellValue = Double.parseDouble((String) cellValue); // String -> Double
                                        break;
                                    // Long
                                    case StringConstant.LONG:
                                    case StringConstant.LONG_PRIMITIVE:
                                        cellValue = Long.parseLong((String) cellValue); // String -> Long
                                        break;
                                    // String
                                    case StringConstant.STRING:
                                        break;
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
     * validateUniqueKey - @UniqueKey 설정된 필드가 보장되는지 검증하는 함수
     *
     *
     * @param objects
     * @param bodyException
     */
    private <T> void validateUniqueKey(List<T> objects,
                                       GroupRegionList groupRegionList,
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
            throw new EOMWrongUniqueFieldException(sheetName, args);
        }

        // init uniqueKey, minGroup
        Map<List<Field>, Integer> uniqueKeyGroupLevelStorage = new HashMap<>(); // 복합키 필드중 MIN 그룹값 저장소
        for (List<String> uniqueKey : uniqueKeys) {
            List<Field> key = new ArrayList<>();
            int minGroupInUniqueKey = this.fieldInfoMap.entrySet().stream()
                    .filter(entry -> uniqueKey.indexOf(entry.getKey().getName()) > -1)
                    .map(entry -> {
                        key.add(entry.getKey());
                        return entry.getValue().getGroup();
                    })
                    .min(Integer::min).get();
            uniqueKeyGroupLevelStorage.put(key, minGroupInUniqueKey);
        }

        // unique validate
        Iterator<Map.Entry<List<Field>, Integer>> iterator = uniqueKeyGroupLevelStorage.entrySet().stream().iterator();
        while (iterator.hasNext()) {
            Map.Entry<List<Field>, Integer> entry = iterator.next();
            List<Field> uniqueKeyByFields = entry.getKey();
            Set<String> uniqueValueStorage = new HashSet<>();
            Integer minGroupInUniqueKey = entry.getValue();
            List<Integer> groupEndPoint = groupRegionList.findGroupRegionEndPoint(minGroupInUniqueKey);

            for (int i = 0; i < objects.size(); i++) {
                T object = objects.get(i);

                // isLowerGroup ? OR isFirstGroup ?
                if(minGroupInUniqueKey == 0 || groupEndPoint.indexOf(i) > 0) {

                    // UniqueKey 조합하여 중복되는 데이터가 있는지 확인하는 절차
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
                            .reduce((a, b) -> a + ExcelConstant.UNIQUE_DILIMETER + b)
                            .get();
                    if (uniqueValueStorage.contains(result + "")) {
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
