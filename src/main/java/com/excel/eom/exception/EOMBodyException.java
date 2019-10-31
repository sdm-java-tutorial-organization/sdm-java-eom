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
        super();
    }

    public EOMBodyException(String message) {
        super(message);
    }

    public EOMBodyException(String message, Throwable cause) {
        super(message, cause);
    }

    public EOMBodyException(Throwable cause) {
        super(cause);
    }

    public EOMBodyException(List objects) {
        super();
        this.objects = objects;
    }

    public int countDetail() {
        return detail.size();
    }

    public void addDetail(EOMCellException e) {
        detail.add(e);
    }

}
