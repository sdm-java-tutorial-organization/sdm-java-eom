package com.excel.eom.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ColumnElement {
    String name;
    Integer index = 0;
    Integer group = 0;
}
