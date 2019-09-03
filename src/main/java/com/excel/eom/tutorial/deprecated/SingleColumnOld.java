package com.excel.eom.tutorial.deprecated;

import com.excel.eom.annotation.deprecated.AnnotationExcelColumn;
import com.excel.eom.constant.deprecated.OldExcelColumn;

// TODO is it possible to force field ? to compile error
@AnnotationExcelColumn(mapping = OldExcelObjectDemo.class)

// TODO is it possible to force annotation ?
//@Getter
//@AllArgsConstructor

@Deprecated
public enum SingleColumnOld implements OldExcelColumn {

    GROUP(0, "group", String.class),
    NAME(1, "name", String.class),
    HEIGHT(2, "height", Integer.class),
    WEIGHT(3, "weight", Integer.class);

    // force field
    SingleColumnOld(int order, String field, Class<?> type) {
        this.order = order;
        this.field = field;
        this.type = type;
    }

    // @_Order
    private int order;

    // @_Field
    private String field;

    // @_DataType
    private Class<?> type;

    public int getOrder() {
        return order;
    }

    public String getField() {
        return field;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public String getPrimaryKey() {
        return null;
    }


}
