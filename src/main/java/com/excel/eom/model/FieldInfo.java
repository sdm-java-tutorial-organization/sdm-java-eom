package com.excel.eom.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;

@Getter
public class FieldInfo implements Comparable<FieldInfo> {

    public static final String INIT_DROPDOWN = "";

    private String name;
    private Integer index;
    private Integer group;
    private String dropdown;

    @Setter
    private Boolean nullable;

    private StyleInfo styleInfo;

    @Getter
    @AllArgsConstructor
    public static class StyleInfo {
        private Integer width;
        private Short alignment;
        private IndexedColors cellColor;
        private short borderStyle;
        private IndexedColors borderColor;
    }

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

    public FieldInfo(String name, Integer index, Integer group, String dropdown, Boolean nullable, Integer width, Short alignment, IndexedColors cellColor, short borderStyle, IndexedColors borderColor) {
        this(name, index, group, dropdown, nullable);
        this.styleInfo = new StyleInfo(width, alignment, cellColor, borderStyle, borderColor);
    }

    @Override
    public int compareTo(FieldInfo o) {
        return Integer.compare(index, o.index);
    }
}
