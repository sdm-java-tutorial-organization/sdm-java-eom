package com.excel.eom.constant.deprecated;

@Deprecated
public interface OldExcelColumn {

    public int getOrder();

    public String getField();

    public Class<?> getType();

    // TODO
    public String getPrimaryKey();

    public static boolean isExcelColumn(Class<?> clazz) {
        return OldExcelColumn.class.isAssignableFrom(clazz) && clazz.isEnum();
    }

}
