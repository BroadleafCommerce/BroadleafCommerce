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

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/**
 * Convenience class to faciliate getting internationalized messages. 
 * 
 * Note that this class is scanned as a bean to pick up the applicationContext, but the methods
 * this class provides should be invoked statically.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Service("blBLCMessageUtils")
public class BLCMessageUtils implements ApplicationContextAware {

    protected static ApplicationContext applicationContext;
    
    /**
     * Returns the message requested by the code with no arguments and the currently set Java Locale on 
     * the {@link BroadleafRequestContext} as returned by {@link BroadleafRequestContext#getJavaLocale()}
     * 
     * @param code
     * @return the message
     */
    public static String getMessage(String code) {
        return getMessage(code, (Object) null);
    }
    
    /**
     * Returns the message requested by the code with the specified arguments and the currently set Java Locale on 
     * the {@link BroadleafRequestContext} as returned by {@link BroadleafRequestContext#getJavaLocale()}
     * 
     * @param code
     * @return the message
     */
    public static String getMessage(String code, Object... args) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        return getMessageSource().getMessage(code, args, brc.getJavaLocale());
    }
    
    /**
     * @return the "messageSource" bean from the application context
     */
    protected static MessageSource getMessageSource() {
        return (MessageSource) applicationContext.getBean("messageSource");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BLCMessageUtils.applicationContext = applicationContext;
    }

}
