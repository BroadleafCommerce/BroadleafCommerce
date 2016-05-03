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

import org.springframework.beans.factory.FactoryBean;

public class EnvironmentFactoryBean implements FactoryBean {

    private String className;

    public EnvironmentFactoryBean(String className) {
        this.className = className;
    }

    public Object getObject() throws Exception {
        return Class.forName(className).newInstance();
    }

    @SuppressWarnings("unchecked")
    public Class getObjectType() {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isSingleton() {
        return false;
    }

}
