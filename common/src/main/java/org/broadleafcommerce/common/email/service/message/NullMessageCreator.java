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
package org.broadleafcommerce.common.email.service.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;

public class NullMessageCreator extends MessageCreator {
    
    private static final Log LOG = LogFactory.getLog(NullMessageCreator.class);
    
    public NullMessageCreator(JavaMailSender mailSender) {
        super(mailSender);  
    }
    
    @Override
    public String buildMessageBody(EmailInfo info, Map<String,Object> props) {
        return info.getEmailTemplate();
    }
    
    @Override
    public void sendMessage(final Map<String,Object> props) throws MailException {
        LOG.warn("NullMessageCreator is defined -- specify a real message creator to send emails");
    }
    
}
