package com.excel.eom.util;

import java.util.Collection;

/**
 * @author redcrow
 */
public class CollectionUtil {

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Object[] object) {
        return object == null || object.length < 1;
    }
}
