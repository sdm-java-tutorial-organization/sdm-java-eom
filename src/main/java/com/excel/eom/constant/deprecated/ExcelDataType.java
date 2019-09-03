package com.excel.eom.constant.deprecated;

import lombok.Getter;

@Deprecated
@Getter
public enum ExcelDataType {
    BOOLEAN(Boolean.class),
    INTEGER(Integer.class),
    DOUBLE(Double.class),
    FLOAT(Float.class),
    STRING(String.class);

    // TODO
    //    NUMBER,
    //    DATE,
    //    DATETIME,
    //    TIMESTAMP;

    Class<?> type;

    ExcelDataType(Class<?> type) {
        this.type = type;
    }

    public String getTypeName() {
        return this.type.getSimpleName();
    }
}
