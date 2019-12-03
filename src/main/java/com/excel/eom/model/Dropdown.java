package com.excel.eom.model;

import com.excel.eom.exception.body.EOMNotContainException;
import lombok.Getter;

import java.util.Iterator;
import java.util.Map;

@Getter
public class Dropdown {

    private final String key;
    private final Map options;

    public Dropdown(String key, Map options) {
        this.key = key;
        this.options = options;
    }

}
