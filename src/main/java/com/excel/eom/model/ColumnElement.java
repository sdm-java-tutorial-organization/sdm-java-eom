package com.excel.eom.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
public class ColumnElement {

    public static final String INIT_DROPDOWN = "";

    private String name;
    private Integer index;
    private Integer group;
    private String dropdown;
    private Boolean nullable;

    public ColumnElement(String name, Integer index, Integer group, String dropdown) {
        this.name = name;
        this.index = index;
        this.group = group;
        this.dropdown = dropdown;
        this.nullable = true;
    }

    public ColumnElement(String name, Integer index, Integer group, String dropdown, Boolean nullable) {
        this.name = name;
        this.index = index;
        this.group = group;
        this.dropdown = dropdown;
        this.nullable = nullable;
    }

}
