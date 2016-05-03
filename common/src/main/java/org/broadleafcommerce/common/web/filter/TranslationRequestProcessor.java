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
package org.broadleafcommerce.common.web.filter;

import org.broadleafcommerce.common.i18n.service.TranslationConsiderationContext;
import org.broadleafcommerce.common.i18n.service.TranslationService;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.AbstractBroadleafWebRequestProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;

/**
 * This processor is responsible for setting up the translation context.   It is intended to be used
 * by both typical Web applications and called from a ServletFilter (such as "TranslationFilter") or 
 * from a portletFilter (such as "TranslationInterceptor")
 * 
 * @author bpolster
 */
@Component("blTranslationRequestProcessor")
public class TranslationRequestProcessor extends AbstractBroadleafWebRequestProcessor {
    
    @Resource(name = "blTranslationService")
    protected TranslationService translationService;
    
    protected boolean getTranslationEnabled() {
        return BLCSystemProperty.resolveBooleanSystemProperty("i18n.translation.enabled");
    }

    @Override
    public void process(WebRequest request) {
        TranslationConsiderationContext.setTranslationConsiderationContext(getTranslationEnabled());
        TranslationConsiderationContext.setTranslationService(translationService);
    }
}
