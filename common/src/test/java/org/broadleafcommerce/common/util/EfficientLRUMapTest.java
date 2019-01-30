/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */

package org.broadleafcommerce.common.util;

import org.apache.commons.collections4.map.LRUMap;
import org.broadleafcommerce.common.util.EfficientLRUMap;

import java.util.concurrent.ConcurrentHashMap;

import junit.framework.TestCase;

public class EfficientLRUMapTest extends TestCase {

    public void testMapSwitch() {
        EfficientLRUMap<String, String> testMap = new EfficientLRUMap<>(5);

        // Test basics for a single name value pair
        testMap.put("key1", "value1");
        assertEquals("The value for key1 should be value 1", "value1", testMap.get("key1"));
        assertEquals("The size() for the map should be 1", 1, testMap.size());
        assertEquals("The type of Map should be ConcurrentHashMap",
                testMap.getUnderlyingMapClass(), ConcurrentHashMap.class);

        // Add keys up to the limit
        testMap.put("key2", "value2");
        testMap.put("key3", "value3");
        testMap.put("key4", "value4");
        testMap.put("key5", "value5");

        // Validate last items and map type.
        assertEquals("The value for key5 should be value5", "value5", testMap.get("key5"));
        assertEquals("The size() for the map should be 5", 5, testMap.size());
        assertEquals("The type of Map should be ConcurrentHashMap",
                testMap.getUnderlyingMapClass(), ConcurrentHashMap.class);

        // Updating an item shouldn't change the map type
        testMap.put("key5", "value5b");
        assertEquals("The value for key5 should now be value5b", "value5b", testMap.get("key5"));
        assertEquals("The size() for the map should be 5", 5, testMap.size());
        assertEquals("The type of Map should be ConcurrentHashMap",
                testMap.getUnderlyingMapClass(), ConcurrentHashMap.class);

        // Add another item which should trigger a switch in the map type
        testMap.put("key6", "value6");
        assertEquals("The value for key6 should be value6", "value6", testMap.get("key6"));
        assertEquals("The size() for the map should be 5 since we are now LRU", 5, testMap.size());
        assertTrue("The type of Map should not be a ConcurrentHashMap.   It should be a synchronized map",
                !testMap.getUnderlyingMapClass().equals(LRUMap.class));
    }
}
