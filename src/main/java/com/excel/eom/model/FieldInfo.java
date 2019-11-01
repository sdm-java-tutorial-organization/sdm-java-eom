package com.excel.eom.model;


import lombok.Getter;
import lombok.Setter;

@Getter
public class FieldInfo implements Comparable<FieldInfo> {

    public static final String INIT_DROPDOWN = "";

    private String name;
    private Integer index;
    private Integer group;
    private String dropdown;

    @Setter
    private Boolean nullable;

    public FieldInfo(String name, Integer index, Integer group, String dropdown) {
        this.name = name;
        this.index = index;
        this.group = group;
        this.dropdown = dropdown;
        this.nullable = true;
    }

    public FieldInfo(String name, Integer index, Integer group, String dropdown, Boolean nullable) {
        this(name, index, group, dropdown);
        this.nullable = nullable;
    }

    @Override
    public int compareTo(FieldInfo o) {
        return Integer.compare(index, o.index);
    }
}
