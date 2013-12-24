/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.exception.SiteNotFoundException;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.service.SandBoxService;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.web.AbstractBroadleafWebRequestProcessor;
import org.broadleafcommerce.common.web.BroadleafCurrencyResolver;
import org.broadleafcommerce.common.web.BroadleafLocaleResolver;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.BroadleafSiteResolver;
import org.broadleafcommerce.common.web.BroadleafTimeZoneResolver;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.TimeZone;

import javax.annotation.Resource;


/**
 * 
 * @author Phillip Verheyden
 * @see {@link org.broadleafcommerce.common.web.BroadleafRequestFilter}
 */
@Component("blAdminRequestProcessor")
public class BroadleafAdminRequestProcessor extends AbstractBroadleafWebRequestProcessor {

    public static final String ADMIN_USER_PROPERTY = "adminUser";
    public static final String SANDBOX_REQ_PARAM = "blSandBoxId";

    protected final Log LOG = LogFactory.getLog(getClass());

    @Resource(name = "blSiteResolver")
    protected BroadleafSiteResolver siteResolver;

    @Resource(name = "messageSource")
    protected MessageSource messageSource;
    
    @Resource(name = "blLocaleResolver")
    protected BroadleafLocaleResolver localeResolver;
    
    @Resource(name = "blAdminTimeZoneResolver")
    protected BroadleafTimeZoneResolver broadleafTimeZoneResolver;

    @Resource(name = "blCurrencyResolver")
    protected BroadleafCurrencyResolver currencyResolver;

    @Resource(name="blSandBoxService")
    protected SandBoxService sandBoxService;

    @Resource(name="blAdminSecurityRemoteService")
    protected SecurityVerifier adminRemoteSecurityService;

    @Override
    public void process(WebRequest request) throws SiteNotFoundException {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc == null) {
            brc = new BroadleafRequestContext();
            BroadleafRequestContext.setBroadleafRequestContext(brc);
        }
        if (brc.getSite() == null) {
            Site site = siteResolver.resolveSite(request);
            brc.setSite(site);
        }
        brc.setWebRequest(request);
        brc.setIgnoreSite(brc.getSite() == null);
        brc.setAdmin(true);

        Locale locale = localeResolver.resolveLocale(request);
        brc.setLocale(locale);
        
        brc.setMessageSource(messageSource);
        
        TimeZone timeZone = broadleafTimeZoneResolver.resolveTimeZone(request);
        brc.setTimeZone(timeZone);

        BroadleafCurrency currency = currencyResolver.resolveCurrency(request);
        brc.setBroadleafCurrency(currency);

        AdminUser adminUser = adminRemoteSecurityService.getPersistentAdminUser();
        if (adminUser != null) {
            brc.getAdditionalProperties().put(ADMIN_USER_PROPERTY, adminUser);
        }
    }

    @Override
    public void postProcess(WebRequest request) {
        ThreadLocalManager.remove();
    }

}
