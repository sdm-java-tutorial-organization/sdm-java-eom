package com.excel.eom.exception.development;

import com.excel.eom.exception.EOMDevelopmentException;
import lombok.Data;

@Data
public class EOMWrongGroupException extends EOMDevelopmentException {

    public EOMWrongGroupException(String message) {
        super(message);
    }

}
