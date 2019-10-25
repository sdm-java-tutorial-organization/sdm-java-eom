package com.excel.eom.exception;

public class EOMDevelopmentException extends EOMRuntimeException {

    public static final String TYPE = "DEV";

    public EOMDevelopmentException(String messqage) {
        super(String.format("[%s]", TYPE) + messqage);
    }

}
