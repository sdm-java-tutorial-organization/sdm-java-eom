package com.excel.eom.exception;

import com.excel.eom.exception.body.EOMNotNullException;
import org.junit.Test;

public class EOMException {

    @Test
    public void eomNotNullException() throws EOMNotNullException {
        Exception eA = new EOMNotNullException();
        eA.printStackTrace();

        Exception eB = new EOMNotNullException("message ~ ");
        eB.printStackTrace();

        Exception eC = new EOMNotNullException(new Exception());
        eC.printStackTrace();

        Exception eD = new EOMNotNullException(new Exception());
        eD.printStackTrace();
    }


}
