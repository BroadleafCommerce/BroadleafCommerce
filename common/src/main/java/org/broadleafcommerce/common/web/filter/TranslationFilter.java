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

import org.broadleafcommerce.common.admin.condition.ConditionalOnNotAdmin;
import org.broadleafcommerce.common.i18n.service.TranslationConsiderationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Responsible for setting the necessary attributes on the {@link TranslationConsiderationContext}.
 * 
 * @author Andre Azzolini (apazzolini), bpolster
 */
@Component("blTranslationFilter")
@ConditionalOnNotAdmin
public class TranslationFilter extends AbstractIgnorableFilter {
    
    @Autowired
    @Qualifier("blTranslationRequestProcessor")
    protected TranslationRequestProcessor translationRequestProcessor;

    @Autowired
    protected Environment env;

    @Override
    public void doFilterUnlessIgnored(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            if (areTranslationsEnabled()) {
                translationRequestProcessor.process(new ServletWebRequest((HttpServletRequest) request, (HttpServletResponse) response));
            }

            filterChain.doFilter(request, response);
        } finally {
            if (areTranslationsEnabled()) {
                translationRequestProcessor.postProcess(new ServletWebRequest((HttpServletRequest) request, (HttpServletResponse) response));
            }
        }
    }

    protected boolean areTranslationsEnabled() {
        return env.getProperty("i18n.translation.enabled", boolean.class, false);
    }

    @Override
    public int getOrder() {
        return FilterOrdered.POST_SECURITY_LOW;
    }
}
