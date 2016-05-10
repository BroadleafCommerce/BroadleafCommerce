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

import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Iterator;
import java.util.Map;

public class ThymeleafMessageCreator extends MessageCreator {

    private TemplateEngine templateEngine;
    
    public ThymeleafMessageCreator(TemplateEngine templateEngine, JavaMailSender mailSender) {
        super(mailSender);
        this.templateEngine = templateEngine;        
    }

    @Override
    public String buildMessageBody(EmailInfo info, Map<String,Object> props) {
        BroadleafRequestContext blcContext = BroadleafRequestContext.getBroadleafRequestContext();
        
        final Context thymeleafContext = new Context();
        if (blcContext != null && blcContext.getJavaLocale() != null) {
            thymeleafContext.setLocale(blcContext.getJavaLocale());             
        }           
        
        if (props != null) {
            Iterator<String> propsIterator = props.keySet().iterator();
            while(propsIterator.hasNext()) {
                String key = propsIterator.next();
                thymeleafContext.setVariable(key, props.get(key));
            }
        }
        
        return this.templateEngine.process( info.getEmailTemplate(), thymeleafContext); 
    }
}
