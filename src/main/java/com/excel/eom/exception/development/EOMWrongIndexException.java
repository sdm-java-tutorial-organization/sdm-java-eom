package com.excel.eom.exception.development;

import com.excel.eom.exception.EOMDevelopmentException;
import lombok.Data;

public class EOMWrongIndexException extends EOMDevelopmentException {

    public EOMWrongIndexException(String message) {
        super(message);
    }

}
