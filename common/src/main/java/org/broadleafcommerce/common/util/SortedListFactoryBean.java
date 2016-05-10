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

import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.core.Ordered;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Jeff Fischer
 */
public class SortedListFactoryBean extends ListFactoryBean {

    @Override
    protected List createInstance() {
        List response = super.createInstance();
        Collections.sort(response, new Comparator<Ordered>() {
            @Override
            public int compare(Ordered o1, Ordered o2) {
                return new Integer(o1.getOrder()).compareTo(o2.getOrder());
            }
        });

        return response;
    }
}
