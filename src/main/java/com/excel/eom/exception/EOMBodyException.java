package com.excel.eom.exception;

import com.excel.eom.exception.body.EOMCellException;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ToString(callSuper = true)
@Getter
public class EOMBodyException extends EOMRuntimeException {

    public static final String MESSAGE = "There is an error in the Excel content. Please check the detail.";

    public static final int CODE = 300;

    private List<EOMCellException> detail = new ArrayList<>();

    public EOMBodyException(String sheet) {
        super(MESSAGE, null, CODE, sheet, null);
    }

    public int countDetail() {
        return detail.size();
    }

    public void addDetail(EOMCellException e) {
        detail.add(e);
    }

}
