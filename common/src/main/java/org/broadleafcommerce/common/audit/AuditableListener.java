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
package org.broadleafcommerce.common.audit;

import org.broadleafcommerce.common.util.BLCFieldUtils;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.BroadleafRequestCustomerResolverImpl;

import java.lang.reflect.Field;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class AuditableListener extends AbstractAuditableListener {

    @PrePersist
    public void setAuditCreatedBy(Object entity) throws Exception {
        setAuditCreatedBy(entity, this.getClass());
    }
    
    @PreUpdate
    public void setAuditUpdatedBy(Object entity) throws Exception {
        setAuditUpdatedBy(entity, this.getClass());
    }

    @Override
    protected void setAuditValueAgent(Field field, Object entity) throws IllegalArgumentException, IllegalAccessException {
        Long customerId = 0L;
        try {
            BroadleafRequestContext requestContext = BroadleafRequestContext.getBroadleafRequestContext();
            if (requestContext != null && requestContext.getWebRequest() != null) {
                Object customer = BroadleafRequestCustomerResolverImpl.getRequestCustomerResolver().getCustomer();
                if (customer != null) {
                    Class<?> customerClass = customer.getClass();
                    Field userNameField = BLCFieldUtils.getSingleField(customerClass, "username");
                    userNameField.setAccessible(true);
                    String username = (String) userNameField.get(customer);
                    if (username != null) {
                        //the customer has been persisted
                        Field idField = BLCFieldUtils.getSingleField(customerClass, "id");
                        idField.setAccessible(true);
                        customerId = (Long) idField.get(customer);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        field.setAccessible(true);
        field.set(entity, customerId);
    }
    
}
