package com.excel.eom.exception;

import com.excel.eom.exception.body.EOMCellException;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;

@Data
public class EOMBodyException extends EOMRuntimeException {

    public String message;

    @Getter
    private List objects;
    private List<EOMCellException> detail = new ArrayList<>();

    public EOMBodyException() {

    }

    public EOMBodyException(List objects) {
        this.objects = objects;
    }

    public int countDetail() {
        return detail.size();
    }

    public void addDetail(EOMCellException e) {
        detail.add(e);
    }

}
