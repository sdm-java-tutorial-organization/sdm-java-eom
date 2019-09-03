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
public class EOMNoObjectException extends EOMRuntimeException {

    public String className;

    public EOMNoObjectException() {
        super();
    }

    public EOMNoObjectException(Throwable e, String className) {
        super(e);
        this.className = className;
    }

    public String getMessage() {
        return String.format("Wrong OldExcelObject (Class : %s)", className);
    }
}
