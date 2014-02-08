/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        return getMessage(code, null);
    }
    
    /**
     * Returns the message requested by the code with the specified arguments and the currently set Java Locale on 
     * the {@link BroadleafRequestContext} as returned by {@link BroadleafRequestContext#getJavaLocale()}
     * 
     * @param code
     * @return the message
     */
    public static String getMessage(String code, Object[] args) {
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
