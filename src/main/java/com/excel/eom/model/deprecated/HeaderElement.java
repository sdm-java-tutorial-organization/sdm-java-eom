package com.excel.eom.model.deprecated;

import lombok.Data;
import lombok.Getter;
import org.apache.poi.ss.util.CellRangeAddress;

@Deprecated
public class HeaderElement {

    public static final int DEFAULT_LEVEL = 0;

    @Getter
    private String name;

    @Getter
    private int order;

    @Getter
    private String field;

    @Getter
    private Class<?> type;

    private Level level = new Level();

    @Data
    private static class Level {
        private int level;
        private CellRangeAddress area;
    }

    public HeaderElement(String name,
                         int order,
                         String field,
                         Class<?> type) {
        this.name = name;
        this.order = order;
        this.field = field;
        this.type = type;
        this.level.setLevel(DEFAULT_LEVEL);
    }

    public HeaderElement(String name,
                         int order,
                         String field,
                         Class<?> type,
                         int level) {
        this.name = name;
        this.order = order;
        this.field = field;
        this.type = type;
        this.level.setLevel(level);
    }

    public int getLevel() {
        return level.getLevel();
    }

    public void setArea(CellRangeAddress area) {
        this.level.setArea(area);
    }

    public boolean compareArea(CellRangeAddress area) {
        if (level.getArea() == null) {
            return this.level.getArea() == area;
        } else {
            return this.level.getArea().getFirstRow() == area.getFirstRow()
                    &&
                    this.level.getArea().getLastRow() == area.getLastRow()
                    &&
                    this.level.getArea().getFirstColumn() == area.getFirstColumn()
                    &&
                    this.level.getArea().getLastColumn() == area.getLastColumn();
        }
    }

}
