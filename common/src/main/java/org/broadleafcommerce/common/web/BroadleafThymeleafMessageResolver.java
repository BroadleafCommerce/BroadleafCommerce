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
package org.broadleafcommerce.common.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.i18n.service.TranslationService;
import org.thymeleaf.Arguments;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.MessageResolution;
import org.thymeleaf.util.Validate;

import java.util.Locale;

import javax.annotation.Resource;

/**
 * This implementation will check to see if the key matches the known i18n value key. If that is the case, we will attempt 
 * to translate the requested field value for the entity/key. If not, we will delegate to other message resolvers.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class BroadleafThymeleafMessageResolver extends AbstractMessageResolver {
    protected static final Log LOG = LogFactory.getLog(BroadleafThymeleafMessageResolver.class);
    protected static final String I18N_VALUE_KEY = "translate";
    
    @Resource(name = "blTranslationService")
    protected TranslationService translationService;
    
    /**
     * Resolve a translated value of an object's property.
     * 
     * @param args
     * @param key
     * @param messageParams
     * @return the resolved message
     */
    public MessageResolution resolveMessage(final Arguments args, final String key, final Object[] messageParams) {
        Validate.notNull(args, "args cannot be null");
        Validate.notNull(args.getContext().getLocale(), "Locale in context cannot be null");
        Validate.notNull(key, "Message key cannot be null");
        
        if (I18N_VALUE_KEY.equals(key)) {
            Object entity = messageParams[0];
            String property = (String) messageParams[1];
            Locale locale = args.getContext().getLocale();
            
            if (LOG.isTraceEnabled()) {
                LOG.trace(String.format("Attempting to resolve translated value for object %s, property %s, locale %s",
                        entity, property, locale));
            }
            
            String resolvedMessage = translationService.getTranslatedValue(entity, property, locale);
            
            if (StringUtils.isNotBlank(resolvedMessage)) {
                return new MessageResolution(resolvedMessage);
            }
        }
        
        return null;
    }

}
