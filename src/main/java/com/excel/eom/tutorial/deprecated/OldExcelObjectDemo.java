package com.excel.eom.tutorial.deprecated;

import com.excel.eom.constant.deprecated.OldExcelObject;
import lombok.Data;

@Deprecated
@Data
public class OldExcelObjectDemo implements OldExcelObject {

    // must be public
    public String group;
    public String name;
    public Integer height;
    public Integer weight;

}
