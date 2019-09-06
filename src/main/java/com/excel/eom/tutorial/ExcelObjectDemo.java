package com.excel.eom.tutorial;


import com.excel.eom.annotation.ExcelColumn;
import com.excel.eom.annotation.ExcelObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

@Data
@NoArgsConstructor // * must have
@AllArgsConstructor
@ExcelObject(
        name = "EOM",
        cellColor = IndexedColors.YELLOW,
        borderColor = IndexedColors.BLACK,
        borderStyle = BorderStyle.THIN)
public class ExcelObjectDemo {

    // TODO index 같을 경우
    // TODO name 같을 경우

    @ExcelColumn(name = "GROUP", group = 1, index = 0)
    public String group;

    @ExcelColumn(name = "NAME", group = 0, index = 1)
    public String name;

    @ExcelColumn(name = "HEIGHT", group = 0, index = 2)
    public Integer height;

    @ExcelColumn(name = "WEIGHT", group = 0, index = 3)
    public Integer weight;

}



