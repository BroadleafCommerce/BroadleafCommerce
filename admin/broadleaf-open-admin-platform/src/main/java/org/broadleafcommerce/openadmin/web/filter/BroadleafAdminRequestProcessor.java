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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.exception.SiteNotFoundException;
import org.broadleafcommerce.common.extension.ExtensionManager;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.common.sandbox.service.SandBoxService;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.web.AbstractBroadleafWebRequestProcessor;
import org.broadleafcommerce.common.web.BroadleafCurrencyResolver;
import org.broadleafcommerce.common.web.BroadleafLocaleResolver;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.BroadleafSandBoxResolver;
import org.broadleafcommerce.common.web.BroadleafSiteResolver;
import org.broadleafcommerce.common.web.BroadleafTimeZoneResolver;
import org.broadleafcommerce.common.web.DeployBehavior;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;


/**
 * 
 * @author Phillip Verheyden
 * @see {@link org.broadleafcommerce.common.web.BroadleafRequestFilter}
 */
@Component("blAdminRequestProcessor")
public class BroadleafAdminRequestProcessor extends AbstractBroadleafWebRequestProcessor {

    public static final String SANDBOX_REQ_PARAM = "blSandBoxId";
    
    public static final String ADMIN_ENFORCE_PRODUCTION_WORKFLOW_KEY = "admin.enforce.production.workflow.update";

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

    @Resource(name = "blSandBoxService")
    protected SandBoxService sandBoxService;

    @Resource(name = "blAdminSecurityRemoteService")
    protected SecurityVerifier adminRemoteSecurityService;
    
    @Resource(name = "blAdminSecurityService")
    protected AdminSecurityService adminSecurityService;
    
    @Value("${" + ADMIN_ENFORCE_PRODUCTION_WORKFLOW_KEY + ":true}")
    protected boolean enforceProductionWorkflowUpdate = true;

    @Value("${enterprise.use.production.sandbox.mode}")
    protected boolean isProductionSandBoxMode;
    
    @Resource(name="blEntityExtensionManagers")
    protected Map<String, ExtensionManager> entityExtensionManagers;

    @Override
    public void process(WebRequest request) throws SiteNotFoundException {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc == null) {
            brc = new BroadleafRequestContext();
            BroadleafRequestContext.setBroadleafRequestContext(brc);
        }
        
        brc.getAdditionalProperties().putAll(entityExtensionManagers);
        
        if (brc.getSite() == null) {
            Site site = siteResolver.resolveSite(request);
            brc.setSite(site);
        }
        brc.setWebRequest(request);
        brc.setIgnoreSite(brc.getSite() == null);
        brc.setAdmin(true);

        brc.getAdditionalProperties().put(ADMIN_ENFORCE_PRODUCTION_WORKFLOW_KEY, enforceProductionWorkflowUpdate);
        
        Locale locale = localeResolver.resolveLocale(request);
        brc.setLocale(locale);
        
        brc.setMessageSource(messageSource);
        
        TimeZone timeZone = broadleafTimeZoneResolver.resolveTimeZone(request);
        brc.setTimeZone(timeZone);

        BroadleafCurrency currency = currencyResolver.resolveCurrency(request);
        brc.setBroadleafCurrency(currency);

        prepareSandBox(request, brc);
    }

    protected void prepareSandBox(WebRequest request, BroadleafRequestContext brc) {
        AdminUser adminUser = adminRemoteSecurityService.getPersistentAdminUser();
        if (adminUser == null) {
            //clear any sandbox
            request.removeAttribute(BroadleafSandBoxResolver.SANDBOX_ID_VAR, WebRequest.SCOPE_GLOBAL_SESSION);
        } else {
            SandBox sandBox = null;
            if (StringUtils.isNotBlank(request.getParameter(SANDBOX_REQ_PARAM))) {
                Long sandBoxId = Long.parseLong(request.getParameter(SANDBOX_REQ_PARAM));
                sandBox = sandBoxService.retrieveUserSandBoxForParent(adminUser.getId(), sandBoxId);
                if (sandBox == null) {
                    SandBox approvalOrUserSandBox = sandBoxService.retrieveSandBoxById(sandBoxId);
                    if (approvalOrUserSandBox.getSandBoxType().equals(SandBoxType.USER)) {
                        sandBox = approvalOrUserSandBox;
                    } else {
                        sandBox = sandBoxService.createUserSandBox(adminUser.getId(), approvalOrUserSandBox);
                    }
                }
            }

            if (sandBox == null) {
                Long previouslySetSandBoxId = (Long) request.getAttribute(BroadleafSandBoxResolver.SANDBOX_ID_VAR,
                        WebRequest.SCOPE_GLOBAL_SESSION);
                if (previouslySetSandBoxId != null) {
                    sandBox = sandBoxService.retrieveSandBoxById(previouslySetSandBoxId);
                }
            }

            if (sandBox == null) {
                List<SandBox> defaultSandBoxes = sandBoxService.retrieveSandBoxesByType(SandBoxType.DEFAULT);
                if (defaultSandBoxes.size() > 1) {
                    throw new IllegalStateException("Only one sandbox should be configured as default");
                }

                SandBox defaultSandBox;
                if (defaultSandBoxes.size() == 1) {
                    defaultSandBox = defaultSandBoxes.get(0);
                } else {
                    defaultSandBox = sandBoxService.createDefaultSandBox();
                }

                sandBox = sandBoxService.retrieveUserSandBoxForParent(adminUser.getId(), defaultSandBox.getId());
                if (sandBox == null) {
                    sandBox = sandBoxService.createUserSandBox(adminUser.getId(), defaultSandBox);
                }
            }

            // If the user just changed sandboxes, we want to update the database record.
            Long previouslySetSandBoxId = (Long) request.getAttribute(BroadleafSandBoxResolver.SANDBOX_ID_VAR, WebRequest.SCOPE_GLOBAL_SESSION);
            if (previouslySetSandBoxId != null && !sandBox.getId().equals(previouslySetSandBoxId)) {
                adminUser.setLastUsedSandBoxId(sandBox.getId());
                adminUser = adminSecurityService.saveAdminUser(adminUser);
            }

            request.setAttribute(BroadleafSandBoxResolver.SANDBOX_ID_VAR, sandBox.getId(), WebRequest.SCOPE_GLOBAL_SESSION);
            brc.setSandBox(sandBox);
            brc.setDeployBehavior(isProductionSandBoxMode ? DeployBehavior.CLONE_PARENT : DeployBehavior.OVERWRITE_PARENT);
            brc.getAdditionalProperties().put("adminUser", adminUser);
        }
    }

    @Override
    public void postProcess(WebRequest request) {
        ThreadLocalManager.remove();
    }

}
