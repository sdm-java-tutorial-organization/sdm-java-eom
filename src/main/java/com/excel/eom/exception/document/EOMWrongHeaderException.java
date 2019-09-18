package com.excel.eom.exception.document;

import com.excel.eom.exception.EOMRuntimeException;
import lombok.Data;

@Data
public class EOMWrongHeaderException extends EOMRuntimeException {

    public String expectedName;
    public String responsedName;

    public EOMWrongHeaderException() {
        super();
    }

    public EOMWrongHeaderException(Throwable e) {
        super(e);
    }

    public EOMWrongHeaderException(Throwable e, String expectedName, String responsedName) {
        super(e);
        this.expectedName = expectedName;
        this.responsedName = responsedName;
    }

    public String getMessage() {
        return String.format("Wrong Header (expected name : %s, responsed name : %s)", expectedName, responsedName);
    }

}
