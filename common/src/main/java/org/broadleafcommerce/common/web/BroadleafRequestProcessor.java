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
import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.RequestDTOImpl;
import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.broadleafcommerce.common.currency.domain.BroadleafRequestedCurrencyDto;
import org.broadleafcommerce.common.extension.ExtensionManager;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.service.SandBoxService;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.Theme;
import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.broadleafcommerce.common.util.DeployBehaviorUtil;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.common.web.exception.HaltFilterChainException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 
 * @author Phillip Verheyden
 * @see {@link BroadleafRequestFilter}
 */
@Component("blRequestProcessor")
public class BroadleafRequestProcessor extends AbstractBroadleafWebRequestProcessor {

    protected final Log LOG = LogFactory.getLog(getClass());

    private static String REQUEST_DTO_PARAM_NAME = BroadleafRequestFilter.REQUEST_DTO_PARAM_NAME;
    public static String REPROCESS_PARAM_NAME = "REPROCESS_BLC_REQUEST";
    
    private static final String SITE_STRICT_VALIDATE_PRODUCTION_CHANGES_KEY = "site.strict.validate.production.changes";
    public static final String SITE_DISABLE_SANDBOX_PREVIEW = "site.disable.sandbox.preview";

    private static final String SANDBOX_ID_PARAM = "blSandboxId";

    @Resource(name = "blSiteResolver")
    protected BroadleafSiteResolver siteResolver;

    @Resource(name = "blLocaleResolver")
    protected BroadleafLocaleResolver localeResolver;

    @Resource(name = "blCurrencyResolver")
    protected BroadleafCurrencyResolver currencyResolver;

    @Resource(name = "blSandBoxResolver")
    protected BroadleafSandBoxResolver sandboxResolver;

    @Resource(name = "blThemeResolver")
    protected BroadleafThemeResolver themeResolver;

    @Resource(name = "messageSource")
    protected MessageSource messageSource;

    @Resource(name = "blTimeZoneResolver")
    protected BroadleafTimeZoneResolver broadleafTimeZoneResolver;

    @Resource(name = "blBaseUrlResolver")
    protected BaseUrlResolver baseUrlResolver;

    @Resource(name = "blSandBoxService")
    protected SandBoxService sandBoxService;
    
    @Value("${thymeleaf.threadLocalCleanup.enabled}")
    protected boolean thymeleafThreadLocalCleanupEnabled = true;

    @Value("${" + SITE_STRICT_VALIDATE_PRODUCTION_CHANGES_KEY + ":false}")
    protected boolean siteStrictValidateProductionChanges = false;

    @Value("${" + SITE_DISABLE_SANDBOX_PREVIEW + ":false}")
    protected boolean siteDisableSandboxPreview = false;

    @Resource(name = "blDeployBehaviorUtil")
    protected DeployBehaviorUtil deployBehaviorUtil;
    
    @Resource(name="blEntityExtensionManagers")
    protected Map<String, ExtensionManager> entityExtensionManagers;
    
    @Override
    public void process(WebRequest request) {
        BroadleafRequestContext brc = new BroadleafRequestContext();
        brc.getAdditionalProperties().putAll(entityExtensionManagers);
        
        Site site = siteResolver.resolveSite(request);
        
        brc.setNonPersistentSite(site);
        brc.setWebRequest(request);
        if (site == null) {
            brc.setIgnoreSite(true);
        }
        brc.setAdmin(false);

        if (siteStrictValidateProductionChanges) {
            brc.setValidateProductionChangesState(ValidateProductionChangesState.SITE);
        } else {
            brc.setValidateProductionChangesState(ValidateProductionChangesState.UNDEFINED);
        }

        BroadleafRequestContext.setBroadleafRequestContext(brc);

        Locale locale = localeResolver.resolveLocale(request);
        brc.setLocale(locale);
        TimeZone timeZone = broadleafTimeZoneResolver.resolveTimeZone(request);
        BroadleafRequestedCurrencyDto currencyDto = currencyResolver.resolveCurrency(request);
        // Assumes BroadleafProcess
        RequestDTO requestDTO = (RequestDTO) request.getAttribute(REQUEST_DTO_PARAM_NAME, WebRequest.SCOPE_REQUEST);
        if (requestDTO == null) {
            requestDTO = new RequestDTOImpl(request);
        }

        SandBox currentSandbox = sandboxResolver.resolveSandBox(request, site);
        
        // When a user elects to switch his sandbox, we want to invalidate the current session. We'll then redirect the 
        // user to the current URL so that the configured filters trigger again appropriately.
        Boolean reprocessRequest = (Boolean) request.getAttribute(BroadleafRequestProcessor.REPROCESS_PARAM_NAME, WebRequest.SCOPE_REQUEST);
        if (reprocessRequest != null && reprocessRequest) {
            LOG.debug("Reprocessing request");
            if (request instanceof ServletWebRequest) {
                HttpServletRequest hsr = ((ServletWebRequest) request).getRequest();
                
                clearBroadleafSessionAttrs(request);
                
                StringBuffer url = hsr.getRequestURL();
                HttpServletResponse response = ((ServletWebRequest) request).getResponse();

                try {
                    if (!isUrlValid(url.toString())) {
                        LOG.error("SECURITY FAILURE Bad redirect location: " + StringUtil.sanitize(url.toString()));
                        response.sendError(403);
                        return;
                    }

                    String sandboxId = hsr.getParameter(SANDBOX_ID_PARAM);

                    if (isSandboxIdValid(sandboxId)) {
                        String queryString = "?" + SANDBOX_ID_PARAM + "=" + sandboxId;
                        url.append(queryString);
                    }

                    response.sendRedirect(url.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                throw new HaltFilterChainException("Reprocess required, redirecting user");
            }
        }
        
        
        if (!siteDisableSandboxPreview && currentSandbox != null) {
            SandBoxContext previewSandBoxContext = new SandBoxContext();
            previewSandBoxContext.setSandBoxId(currentSandbox.getId());
            previewSandBoxContext.setPreviewMode(true);
            SandBoxContext.setSandBoxContext(previewSandBoxContext);
        }
        if (currencyDto != null) {
            brc.setBroadleafCurrency(currencyDto.getCurrencyToUse());
            brc.setRequestedBroadleafCurrency(currencyDto.getRequestedCurrency());
        }
        //We do this to prevent lazy init exceptions when this context/sandbox combination
        // is used in a different session that it was initiated in. see QA#2576
        if(currentSandbox != null && currentSandbox.getChildSandBoxes() != null) {
            currentSandbox.getChildSandBoxes().size();
        }

        brc.setSandBox(currentSandbox);
        brc.setDeployBehavior(deployBehaviorUtil.isProductionSandBoxMode() ? DeployBehavior.CLONE_PARENT : DeployBehavior.OVERWRITE_PARENT);

        // Note that this must happen after the request context is set up as resolving a theme is dependent on site
        Theme theme = themeResolver.resolveTheme(request);
        brc.setTheme(theme);

        brc.setMessageSource(messageSource);
        brc.setTimeZone(timeZone);
        brc.setRequestDTO(requestDTO);
        Map<String, Object> ruleMap = (Map<String, Object>) request.getAttribute("blRuleMap", WebRequest.SCOPE_REQUEST);
        if (ruleMap == null) {
            LOG.trace("Creating ruleMap and adding in Locale.");
            ruleMap = new HashMap<String, Object>();
            request.setAttribute("blRuleMap", ruleMap, WebRequest.SCOPE_REQUEST);
        } else {
            LOG.trace("Using pre-existing ruleMap - added by non standard BLC process.");
        }
        ruleMap.put("locale", locale);

        String adminUserId = request.getParameter(BroadleafRequestFilter.ADMIN_USER_ID_PARAM_NAME);
        if (StringUtils.isNotBlank(adminUserId)) {
            //TODO: Add token logic to secure the admin user id
            brc.setAdminUserId(Long.parseLong(adminUserId));
        }

    }

    protected boolean isUrlValid(String url) {
        boolean isValid = false;
        String siteBaseUrl = baseUrlResolver.getSiteBaseUrl() + "/";

        if (StringUtils.equals(url, siteBaseUrl)) {
            isValid = true;
        }

        return isValid;
    }

    protected boolean isSandboxIdValid(String sandboxId) {
        boolean isValid = false;

        if (StringUtils.isNotEmpty(sandboxId)) {
            Long id = Long.valueOf(sandboxId);

            SandBox sandbox = sandBoxService.retrieveSandBoxById(id);

            if (sandbox != null) {
                isValid = true;
            }
        }

        return isValid;
    }

    @Override
    public void postProcess(WebRequest request) {
        ThreadLocalManager.remove();
    }
    
    protected void clearBroadleafSessionAttrs(WebRequest request) {
        if (BLCRequestUtils.isOKtoUseSession(request)) {
            request.removeAttribute(BroadleafLocaleResolverImpl.LOCALE_VAR, WebRequest.SCOPE_GLOBAL_SESSION);
            request.removeAttribute(BroadleafCurrencyResolverImpl.CURRENCY_VAR, WebRequest.SCOPE_GLOBAL_SESSION);
            request.removeAttribute(BroadleafTimeZoneResolverImpl.TIMEZONE_VAR, WebRequest.SCOPE_GLOBAL_SESSION);
            request.removeAttribute(BroadleafSandBoxResolver.SANDBOX_ID_VAR, WebRequest.SCOPE_GLOBAL_SESSION);

            // From CustomerStateRequestProcessorImpl, using explicit String because it's out of module
            request.removeAttribute("_blc_anonymousCustomer", WebRequest.SCOPE_GLOBAL_SESSION);
            request.removeAttribute("_blc_anonymousCustomerId", WebRequest.SCOPE_GLOBAL_SESSION);
        }
    }
}
