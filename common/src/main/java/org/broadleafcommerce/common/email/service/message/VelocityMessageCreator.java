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

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class VelocityMessageCreator extends MessageCreator {

    private VelocityEngine velocityEngine;
    private Map<String, Object> additionalConfigItems;
    
    public VelocityMessageCreator(VelocityEngine velocityEngine, JavaMailSender mailSender, Map<String, Object> additionalConfigItems) {
        super(mailSender);
        this.additionalConfigItems = additionalConfigItems;
        this.velocityEngine = velocityEngine;        
    }

    @Override
    public String buildMessageBody(EmailInfo info, Map<String,Object> props) {
        if (props == null) {
            props = new HashMap<String, Object>();
        }

        if (props instanceof HashMap) {
            HashMap<String, Object> hashProps = (HashMap<String, Object>) props;
            @SuppressWarnings("unchecked")
            Map<String,Object> propsCopy = (Map<String, Object>) hashProps.clone();
            if (additionalConfigItems != null) {
                propsCopy.putAll(additionalConfigItems);
            }
            StringWriter result = new StringWriter();
            VelocityContext velocityContext = new VelocityContext(propsCopy);
            try {
                velocityEngine.mergeTemplate(info.getEmailTemplate(), info.getEncoding(), velocityContext, result);
            } catch (Exception e) {
                throw ExceptionHelper.refineException(e);
            }
            return result.toString();
        }

        throw new IllegalArgumentException("Property map must be of type HashMap<String, Object>");
    }

    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public Map<String, Object> getAdditionalConfigItems() {
        return additionalConfigItems;
    }

    public void setAdditionalConfigItems(
            Map<String, Object> additionalConfigItems) {
        this.additionalConfigItems = additionalConfigItems;
    }   
}
