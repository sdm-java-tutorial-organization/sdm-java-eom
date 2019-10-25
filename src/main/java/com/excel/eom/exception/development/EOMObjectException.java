package com.excel.eom.exception.development;

import com.excel.eom.exception.EOMDevelopmentException;
import com.excel.eom.exception.EOMRuntimeException;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class EOMObjectException extends EOMDevelopmentException {

    public EOMObjectException(String message) {
        super(message);
    }

}
