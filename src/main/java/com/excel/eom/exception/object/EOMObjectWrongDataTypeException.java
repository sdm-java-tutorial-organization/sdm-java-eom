package com.excel.eom.exception.object;

import com.excel.eom.exception.EOMRuntimeException;
import lombok.Data;

/**
 * Wrong DataType when create excelObject
 *
 * - NumberFormatException
 * - IllegalArgumentException
 * */
@Data
public class EOMObjectWrongDataTypeException extends EOMRuntimeException {

    public String requiredType = "";
    public String responsedType = "";

    public EOMObjectWrongDataTypeException() {
        super();
    }

    public EOMObjectWrongDataTypeException(Throwable e) {
        super(e);
    }

    public EOMObjectWrongDataTypeException(Throwable e, String requiredType, String responsedType) {
        super(e);
        this.requiredType = requiredType;
        this.responsedType = responsedType;
    }

    public String getMessage() {
        return String.format("%s Wrong DataType (required type : %s, responsedType : %s)", message, requiredType, responsedType);
    }


}
