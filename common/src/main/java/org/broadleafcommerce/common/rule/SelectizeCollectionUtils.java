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
package org.broadleafcommerce.common.rule;

import org.apache.commons.collections4.CollectionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link org.apache.commons.collections4.CollectionUtils} wrapper
 * to support collection intersection methods needed for rule builder selectize components.
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class SelectizeCollectionUtils {

    public static <O> Collection<O> intersection(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        return CollectionUtils.intersection(a, b);
    }

    public static Collection intersection(final String a, final Iterable b) {
        List<String> temp = new ArrayList<>();
        temp.add(a);
        return CollectionUtils.intersection(temp, b);
    }

}
