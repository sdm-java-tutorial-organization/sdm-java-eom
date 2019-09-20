package com.excel.eom.exception;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EOMObjectException extends EOMRuntimeException {

    public String message;

}
