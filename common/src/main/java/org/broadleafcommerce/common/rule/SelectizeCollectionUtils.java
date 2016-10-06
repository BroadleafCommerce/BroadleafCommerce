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
package org.broadleafcommerce.common.rule;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.broadleafcommerce.common.value.ValueAssignable;

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

    public static Collection intersection(final Iterable a, final Iterable b) {
        Collection response;
        if (!IterableUtils.isEmpty(a) && (a instanceof ArrayList) && !IterableUtils.isEmpty(b) && (b instanceof ArrayList)) {
            //TODO this is a bit of a hack to allow the intersection of two collections of different types. This is primarily
            //used to facilitate some MVEL execution. We really should be fixing the MVEL to call a method that retrieves
            //a list of Strings, rather than a list of ValueAssignables.
            Object aVal = ((ArrayList) a).get(0);
            Object bVal = ((ArrayList) b).get(0);
            if (aVal instanceof ValueAssignable && bVal instanceof String) {
                response = valueAssignableIntersection(a, b);
            } else {
                response = CollectionUtils.intersection(a, b);
            }
        } else {
            if (a == null || b == null) {
                return CollectionUtils.emptyCollection();
            }
            response = CollectionUtils.intersection(a, b);
        }
        return response;
    }

    public static Collection<String> valueAssignableIntersection(final Iterable<ValueAssignable> a, final Iterable<String> b) {
        List<String> temp = new ArrayList<>();

        if (!IterableUtils.isEmpty(a)) {
            for (ValueAssignable alist : a) {
                temp.add((String) alist.getValue());
            }
        }
        return CollectionUtils.intersection(temp, b);
    }

    public static Collection<String> intersection(final String a, final Iterable<String> b) {
        List<String> temp = new ArrayList<>();
        temp.add(a);
        return CollectionUtils.intersection(temp, b);
    }

}
