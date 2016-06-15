package org.broadleafcommerce.common.util;

/**
 * Convenience methods for interacting with Java number types
 * 
 * @author Daniel Colgrove (dcolgrove)
 */
public class BLCNumberUtils {
    
    /**
     * Given an Object of type Integer or Long, converts the Object instance to a Long.  This will throw a ClassCastException
     * if the past parameter is not either an Integer or a Long.
     * 
     * @param Object
     * @return Long
     */
    public static Long toLong(Object objectToConvert) {
        Long convertedLongValue;
        if (objectToConvert instanceof Integer) {
            convertedLongValue = new Long((Integer) objectToConvert);
        } else {
            convertedLongValue = (Long) objectToConvert;
        }
        return convertedLongValue;
    }
}
