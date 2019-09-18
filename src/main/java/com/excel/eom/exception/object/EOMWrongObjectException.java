package com.excel.eom.exception.object;

import com.excel.eom.exception.EOMRuntimeException;
import lombok.Data;

/**
 * Wrong OldExcelObject
 *
 * - InstantiationException (추상구현체)
 * - IllegalAccessException (접근범위)
 * */
@Data
public class EOMWrongObjectException extends EOMRuntimeException {

    public String className;

    public EOMWrongObjectException() {
        super();
    }

    public EOMWrongObjectException(Throwable e, String className) {
        super(e);
        this.className = className;
    }

    public String getMessage() {
        return String.format("Wrong OldExcelObject (Class : %s)", className);
    }
}
