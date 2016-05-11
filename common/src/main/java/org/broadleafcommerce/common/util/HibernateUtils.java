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

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

/**
 * Hibernate convenience methods
 *
 * @author Philip Baggett (pbaggett)
 */
public class HibernateUtils {
    /**
     * <p>Ensure a domain object is an actual persisted object and not a Hibernate proxy object by getting its real implementation.
     *
     * <p>This is primarily useful when retrieving a lazy loaded object that has been subclassed and you have the intention of casting it.
     *
     * @param t the domain object to deproxy
     * @return the actual persisted object or the passed in object if it is not a Hibernate proxy
     */
    public static <T> T deproxy(T t) {
        if (t instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy)t;
            LazyInitializer lazyInitializer = proxy.getHibernateLazyInitializer();
            return (T)lazyInitializer.getImplementation();
        }
        return t;
    }
}
