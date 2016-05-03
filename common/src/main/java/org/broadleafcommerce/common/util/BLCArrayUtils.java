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

import java.util.ArrayList;
import java.util.HashSet;



/**
 * Convenience methods for interacting with arrays
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class BLCArrayUtils {
    
    /**
     * Given an array and a typed predicate, determines if the array has an object that matches the condition of the
     * predicate. The predicate should evaluate to true when a match occurs.
     * 
     * @param array
     * @param predicate
     * @return whether or not the array contains an element that matches the predicate
     */
    public static <T> boolean contains(T[] array, TypedPredicate<T> predicate) {
        for (T o : array) {
            if (predicate.evaluate(o)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Given an input array, will return an ArrayList representation of the array.
     * 
     * @param array
     * @return the ArrayList corresponding to the input array. If the input is null, this also returns null. If it is empty
     * then this will return an empty list
     */
    public static <T> ArrayList<T> asList(T[] array) {
        if (array == null) {
            return null;
        }
        ArrayList<T> list = new ArrayList<T>(array.length);
        for (T e : array) {
            list.add(e);
        }
        return list;
    }
    
    /**
     * Similar to the CollectionUtils collect except that it works on an array instead of a Java Collection
     * 
     * @param array
     * @param transformer
     * @return the transformed collection
     */
    public static <T, O> ArrayList<T> collect(Object[] array, TypedTransformer<T> transformer) {
        ArrayList<T> list = new ArrayList<T>(array.length);
        for (Object o : array) {
            list.add(transformer.transform(o));
        }
        return list;
    }

    /**
     * The same as {@link #collect(Object[], TypedTransformer)} but returns a set.
     * 
     * @param array
     * @param transformer
     * @return the transformed set
     */
    public static <T, O> HashSet<T> collectSet(Object[] array, TypedTransformer<T> transformer) {
        HashSet<T> set = new HashSet<T>(array.length);
        for (Object o : array) {
            set.add(transformer.transform(o));
        }
        return set;
    }

}
