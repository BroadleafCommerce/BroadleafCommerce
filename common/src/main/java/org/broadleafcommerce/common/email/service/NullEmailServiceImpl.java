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
package org.broadleafcommerce.common.email.service;

import org.broadleafcommerce.common.email.domain.EmailTarget;
import org.broadleafcommerce.common.email.service.info.EmailInfo;

import java.util.Map;

/**
 * This null implementation class can be used during development to work around
 * the need to have a working SMTP service to route emails through.
 * 
 * @author aazzolini
 */
public class NullEmailServiceImpl implements EmailService {

    @Override
    public boolean sendTemplateEmail(String emailAddress, EmailInfo emailInfo, Map<String, Object> props) {
        return true;
    }

    @Override
    public boolean sendTemplateEmail(EmailTarget emailTarget, EmailInfo emailInfo, Map<String, Object> props) {
        return true;
    }

    @Override
    public boolean sendBasicEmail(EmailInfo emailInfo, EmailTarget emailTarget, Map<String, Object> props) {
        return true;
    }

}
