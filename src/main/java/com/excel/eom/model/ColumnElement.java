package com.excel.eom.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ColumnElement implements Comparable<ColumnElement> {

    public static final String INIT_DROPDOWN = "";

    private String name;
    private Integer index;
    private Integer group;
    private String dropdown;

    @Setter
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

    @Override
    public int compareTo(ColumnElement o) {
        return Integer.compare(index, o.index);
    }
}
