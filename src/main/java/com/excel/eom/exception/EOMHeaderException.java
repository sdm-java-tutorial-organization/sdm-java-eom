package com.excel.eom.exception;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EOMHeaderException extends EOMRuntimeException {

    public String message;
    private List<EOMHeaderException> detail = new ArrayList<>();

    public EOMHeaderException() {
        super();
    }

    public EOMHeaderException(Throwable cause) {
        super(cause);
    }

    public int countDetail() {
        return detail.size();
    }

    public void addDetail(EOMHeaderException e) {
        detail.add(e);
    }

}
