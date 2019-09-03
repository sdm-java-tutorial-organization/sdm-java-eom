package com.excel.eom.util.callback;

import java.lang.reflect.Field;

public interface RegionSettingCallback {

    void setRegionInfo(Field field, Object instance, Object value) throws Throwable;

}
