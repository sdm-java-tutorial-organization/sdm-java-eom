package com.excel.eom.exception.object;

import com.excel.eom.exception.EOMRuntimeException;
import lombok.Data;

/**
 * NotFound ColumnInfo In OldExcelObject
 *
 * - NoSuchFieldException
 * - SecurityException
 * - IllegalAccessException
 * */
@Data
public class EOMObjectNotFoundFieldException extends EOMRuntimeException {

    public String className;
    public String fieldName;

    public EOMObjectNotFoundFieldException() {
        super();
    }

    public EOMObjectNotFoundFieldException(Throwable e) {
        super(e);
    }

    public EOMObjectNotFoundFieldException(Throwable e, String className, String fieldName) {
        super(e);
        this.className = className;
        this.fieldName = fieldName;
    }

    @Override
    public String getMessage() {
        return String.format("NotFound ColumnInfo In OldExcelObject (Class : %s, Field : %s)", className, fieldName);
    }
}
