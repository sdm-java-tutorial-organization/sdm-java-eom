package com.excel.eom.exception.development;

import com.excel.eom.exception.EOMDevelopmentException;

public class EOMWrongUniqueFieldException extends EOMDevelopmentException {

    public EOMWrongUniqueFieldException() {
        super("Wrong Field in UniqueKey Annotation.");
    }

}
