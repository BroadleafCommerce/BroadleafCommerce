/*
 * #%L
 * BroadleafCommerce Framework
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
