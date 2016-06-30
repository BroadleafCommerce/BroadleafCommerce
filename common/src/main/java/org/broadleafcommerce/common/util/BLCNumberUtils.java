/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
