/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * Convenience methods for interacting with collections.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class BLCCollectionUtils {
    
    /**
     * Delegates to {@link CollectionUtils#collect(Collection, Transformer)}, but performs the necessary type coercion 
     * to allow the returned collection to be correctly casted based on the TypedTransformer.
     * 
     * @param inputCollection
     * @param transformer
     * @return the typed, collected Collection
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Collection<T> collect(Collection inputCollection, TypedTransformer<T> transformer) {
        return CollectionUtils.collect(inputCollection, transformer);
    }

    /**
     * The same as {@link #collect(Collection, TypedTransformer)} but returns an ArrayList
     * @param inputCollection
     * @param transformer
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <T> List<T> collectList(Collection inputCollection, TypedTransformer<T> transformer) {
        List<T> returnList = new ArrayList<T>();
        for (Object obj : inputCollection) {
            T transformed = transformer.transform(obj);
            returnList.add(transformed);
        }
        return returnList;
    }

    /**
     * The same as {@link #collect(Collection, TypedTransformer)} but returns an array
     * @param inputCollection
     * @param transformer
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> T[] collectArray(Collection inputCollection, TypedTransformer<T> transformer, Class<T> clazz) {
        T[] returnArray = (T[]) Array.newInstance(clazz, inputCollection.size());
        int i = 0;
        for (Object obj : inputCollection) {
            T transformed = transformer.transform(obj);
            returnArray[i++] = transformed;
        }
        return returnArray;
    }

    /**
     * Delegates to {@link CollectionUtils#select(Collection, org.apache.commons.collections.Predicate)}, but will
     * force the return type to be a List<T>.
     * 
     * @param inputCollection
     * @param predicate
     * @return
     */
    public static <T> List<T> selectList(Collection<T> inputCollection, TypedPredicate<T> predicate) {
        ArrayList<T> answer = new ArrayList<T>(inputCollection.size());
        CollectionUtils.select(inputCollection, predicate, answer);
        return answer;
    }
    
    /**
     * It is common to want to make sure that a collection you receive is not null. Instead, we'd rather have
     * an empty list.
     * 
     * @param list
     * @return the passed in list if not null, otherwise a new ArrayList of the same type
     */
    public static <T> List<T> createIfNull(List<T> list) {
        return (list == null) ? new ArrayList<T>() : list;
    }

    /**
     * Create a collection proxy that will perform some piece of work whenever modification methods are called on the
     * proxy. This includes the add, allAll, remove, removeAll, clear methods. Additionally, calling remove on an iterator
     * created from this collection is also covered.
     *
     * @param work the work to perform on collection modification
     * @param original the original collection to make change aware
     * @param <T> the collection type (e.g. List, Set, etc...)
     * @return the proxied collection
     */
    public static <T extends Collection> T createChangeAwareCollection(final WorkOnChange work, final Collection original) {
        T proxy = (T) Proxy.newProxyInstance(BLCCollectionUtils.class.getClassLoader(), ClassUtils.getAllInterfacesForClass(original.getClass()), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().startsWith("add") || method.getName().startsWith("remove") || method.getName()
                        .startsWith("clear")) {
                    work.doWork(original);
                }
                if (method.getName().equals("iterator")) {
                    final Iterator itr = (Iterator) method.invoke(original, args);
                    Iterator proxyItr = (Iterator) Proxy.newProxyInstance(getClass().getClassLoader(), ClassUtils.getAllInterfacesForClass(itr.getClass()), new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (method.getName().equals("remove")) {
                                work.doWork(original);
                            }
                            return method.invoke(itr, args);
                        }
                    });
                    return proxyItr;
                }
                return method.invoke(original, args);
            }
        });
        return proxy;
    }
}
