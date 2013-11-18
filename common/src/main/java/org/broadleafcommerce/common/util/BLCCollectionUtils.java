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

import java.util.ArrayList;
import java.util.Collection;
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

}
