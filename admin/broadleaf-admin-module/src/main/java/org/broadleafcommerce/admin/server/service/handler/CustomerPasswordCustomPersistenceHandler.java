/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.util.PasswordReset;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 
 * @author jfischer
 *
 */
@Component("blCustomerPasswordCustomPersistenceHandler")
public class CustomerPasswordCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {
    
    @Resource(name="blCustomerService")
    protected CustomerService customerService;

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        String[] customCriteria = persistencePackage.getCustomCriteria();
        return customCriteria != null && customCriteria.length > 0 && customCriteria[0].equals("passwordUpdate");
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        Customer customer = customerService.readCustomerByUsername(entity.findProperty("username").getValue());
        if (StringUtils.isEmpty(customer.getEmailAddress())) {
            throw new ServiceException("Unable to update password because an email address is not available for this customer. An email address is required to send the customer the new system generated password.");
        }
        
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setUsername(entity.findProperty("username").getValue());
        passwordReset.setPasswordChangeRequired(false);
        passwordReset.setEmail(customer.getEmailAddress());
        passwordReset.setPasswordLength(22);
        passwordReset.setSendResetEmailReliableAsync(false);
        
        customer = customerService.resetPassword(passwordReset);
        
        return entity;
    }
    
    
    
}
