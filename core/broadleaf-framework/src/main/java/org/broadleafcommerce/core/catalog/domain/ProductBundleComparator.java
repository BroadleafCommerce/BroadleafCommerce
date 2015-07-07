/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.catalog.domain;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Created by bpolster.
 */
public class ProductBundleComparator implements Comparator<ProductBundle> {


    @Override
    public int compare(ProductBundle productBundle, ProductBundle productBundle1) {
        if (productBundle == null && productBundle1 == null) {
            return 0;
        }

        if (productBundle == null) {
            return 1;
        }

        if (productBundle1 == null) {
            return -1;
        }

        int priorityCompare = comparePriorities(productBundle, productBundle1);

        if (priorityCompare == 0) {
            return compareSavings(productBundle, productBundle1);
        } else {
            return priorityCompare;
        }
    }

    private int comparePriorities(ProductBundle productBundle, ProductBundle productBundle1) {
        Integer priority1 = productBundle.getPriority();
        Integer priority2 = productBundle.getPriority();

        if (priority1 == null && priority2 == null) {
            return 0;
        }

        if (priority1 == null) {
            return priority2.compareTo(0) * -1;
        }

        if (priority2 == null) {
            return priority1.compareTo(0);
        }

        return priority1.compareTo(priority2);
    }

    private int compareSavings(ProductBundle productBundle, ProductBundle productBundle1) {
        BigDecimal savings1 = productBundle.getPotentialSavings();
        BigDecimal savings2 = productBundle1.getPotentialSavings();


        if (savings1 == null && savings2 == null) {
            return 0;
        }

        if (savings1 == null) {
            return savings2.compareTo(BigDecimal.ZERO) * -1;
        }

        if (savings2 == null) {
            return savings1.compareTo(BigDecimal.ZERO);
        }

        return savings1.compareTo(savings2);
    }

}
