/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
     * @return the ArrayList corresponding to the input array
     */
    public static <T> ArrayList<T> asList(T[] array) {
        if (array == null || array.length == 0) {
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
