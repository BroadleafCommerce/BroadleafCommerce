/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.filter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.broadleafcommerce.common.currency.domain.BroadleafRequestedCurrencyDto;
import org.broadleafcommerce.common.exception.SiteNotFoundException;
import org.broadleafcommerce.common.extension.ExtensionManager;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.common.sandbox.service.SandBoxService;
import org.broadleafcommerce.common.security.service.StaleStateProtectionService;
import org.broadleafcommerce.common.security.service.StaleStateServiceException;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.service.SiteService;
import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.broadleafcommerce.common.util.DeployBehaviorUtil;
import org.broadleafcommerce.common.web.AbstractBroadleafWebRequestProcessor;
import org.broadleafcommerce.common.web.BroadleafCurrencyResolver;
import org.broadleafcommerce.common.web.BroadleafLocaleResolver;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.BroadleafSandBoxResolver;
import org.broadleafcommerce.common.web.BroadleafSiteResolver;
import org.broadleafcommerce.common.web.BroadleafTimeZoneResolver;
import org.broadleafcommerce.common.web.DeployBehavior;
import org.broadleafcommerce.common.web.ValidateProductionChangesState;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public static final String PROFILE_REQ_PARAM = "blProfileId";
    public static final String CATALOG_REQ_PARAM = "blCatalogId";

    private static final String ADMIN_STRICT_VALIDATE_PRODUCTION_CHANGES_KEY = "admin.strict.validate.production.changes";

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

    @Resource(name = "blSiteService")
    protected SiteService siteService;

    @Resource(name = "blAdminSecurityRemoteService")
    protected SecurityVerifier adminRemoteSecurityService;
    
    @Resource(name = "blAdminSecurityService")
    protected AdminSecurityService adminSecurityService;

    @Resource(name = "blDeployBehaviorUtil")
    protected DeployBehaviorUtil deployBehaviorUtil;
    
    @Value("${" + ADMIN_STRICT_VALIDATE_PRODUCTION_CHANGES_KEY + ":true}")
    protected boolean adminStrictValidateProductionChanges = true;
    
    @Resource(name="blEntityExtensionManagers")
    protected Map<String, ExtensionManager<?>> entityExtensionManagers;

    @Resource(name = "blAdminRequestProcessorExtensionManager")
    protected AdminRequestProcessorExtensionManager extensionManager;

    @Resource(name = "blStaleStateProtectionService")
    protected StaleStateProtectionService staleStateProtectionService;

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

        if (adminStrictValidateProductionChanges) {
            brc.setValidateProductionChangesState(ValidateProductionChangesState.ADMIN);
        } else {
            brc.setValidateProductionChangesState(ValidateProductionChangesState.UNDEFINED);
        }
        
        Locale locale = localeResolver.resolveLocale(request);
        brc.setLocale(locale);
        
        brc.setMessageSource(messageSource);
        
        TimeZone timeZone = broadleafTimeZoneResolver.resolveTimeZone(request);
        brc.setTimeZone(timeZone);

        // Note: The currencyResolver will set the currency on the BroadleafRequestContext but 
        // later modules (specifically PriceListRequestProcessor in BLC enterprise) may override based
        // on the desired currency.
        BroadleafRequestedCurrencyDto dto = currencyResolver.resolveCurrency(request);
        if (dto != null) {
            brc.setBroadleafCurrency(dto.getCurrencyToUse());
            brc.setRequestedBroadleafCurrency(dto.getRequestedCurrency());
        }

        AdminUser adminUser = adminRemoteSecurityService.getPersistentAdminUser();
        if (adminUser != null) {
            brc.setAdminUserId(adminUser.getId());
        }

        prepareSandBox(request, brc);
        prepareProfile(request, brc);
        prepareCatalog(request, brc);

        brc.getAdditionalProperties().put(staleStateProtectionService.getStateVersionTokenParameter(), staleStateProtectionService.getStateVersionToken());
    }

    protected void prepareProfile(WebRequest request, BroadleafRequestContext brc) {
        AdminUser adminUser = adminRemoteSecurityService.getPersistentAdminUser();
        if (adminUser == null) {
            //clear any profile
            if (BLCRequestUtils.isOKtoUseSession(request)) {
                request.removeAttribute(PROFILE_REQ_PARAM, WebRequest.SCOPE_SESSION);
            }
        } else {
            Site profile = null;
            if (StringUtils.isNotBlank(request.getParameter(PROFILE_REQ_PARAM))) {
                Long profileId = Long.parseLong(request.getParameter(PROFILE_REQ_PARAM));
                profile = siteService.retrievePersistentSiteById(profileId);
                if (profile == null) {
                    throw new IllegalArgumentException(String.format("Unable to find the requested profile: %s", profileId));
                }
                String token = request.getParameter(staleStateProtectionService.getStateVersionTokenParameter());
                staleStateProtectionService.compareToken(token);
                staleStateProtectionService.invalidateState(true);
            }

            if (profile == null) {
                Long previouslySetProfileId = null;
                if (BLCRequestUtils.isOKtoUseSession(request)) {
                    previouslySetProfileId = (Long) request.getAttribute(PROFILE_REQ_PARAM,
                        WebRequest.SCOPE_SESSION);
                }
                if (previouslySetProfileId != null) {
                    profile = siteService.retrievePersistentSiteById(previouslySetProfileId);
                }
            }

            if (profile == null) {
                List<Site> profiles = new ArrayList<Site>();
                if (brc.getNonPersistentSite() != null) {
                    Site currentSite = siteService.retrievePersistentSiteById(brc.getNonPersistentSite().getId());
                    if (extensionManager != null) {
                        ExtensionResultHolder<Set<Site>> profilesResult = new ExtensionResultHolder<Set<Site>>();
                        extensionManager.retrieveProfiles(currentSite, profilesResult);
                        if (!CollectionUtils.isEmpty(profilesResult.getResult())) {
                            profiles.addAll(profilesResult.getResult());
                        }
                    }
                }
                if (profiles.size() > 0) {
                    profile = profiles.get(0);
                }
            }

            if (profile != null) {
                if (BLCRequestUtils.isOKtoUseSession(request)) {
                    request.setAttribute(PROFILE_REQ_PARAM, profile.getId(), WebRequest.SCOPE_SESSION);
                }
                brc.setCurrentProfile(profile);
            }
        }
    }

    protected void prepareCatalog(WebRequest request, BroadleafRequestContext brc) {
        AdminUser adminUser = adminRemoteSecurityService.getPersistentAdminUser();
        if (adminUser == null) {
            //clear any catalog
            if (BLCRequestUtils.isOKtoUseSession(request)) {
                request.removeAttribute(CATALOG_REQ_PARAM, WebRequest.SCOPE_SESSION);
            }
        } else {
            Catalog catalog = null;
            if (StringUtils.isNotBlank(request.getParameter(CATALOG_REQ_PARAM))) {
                Long catalogId = Long.parseLong(request.getParameter(CATALOG_REQ_PARAM));
                catalog = siteService.findCatalogById(catalogId);
                if (catalog == null) {
                    throw new IllegalArgumentException(String.format("Unable to find the requested catalog: %s", catalogId));
                }
                String token = request.getParameter(staleStateProtectionService.getStateVersionTokenParameter());
                staleStateProtectionService.compareToken(token);
                staleStateProtectionService.invalidateState(true);
            }

            if (catalog == null) {
                Long previouslySetCatalogId = null;
                if (BLCRequestUtils.isOKtoUseSession(request)) {
                    previouslySetCatalogId = (Long) request.getAttribute(CATALOG_REQ_PARAM,
                        WebRequest.SCOPE_SESSION);
                }
                if (previouslySetCatalogId != null) {
                    catalog = siteService.findCatalogById(previouslySetCatalogId);
                }
            }

            if (catalog == null) {
                List<Catalog> catalogs = new ArrayList<Catalog>();
                if (brc.getNonPersistentSite() != null) {
                    Site currentSite = siteService.retrievePersistentSiteById(brc.getNonPersistentSite().getId());
                    if (extensionManager != null) {
                        ExtensionResultHolder<Set<Catalog>> catalogResult = new ExtensionResultHolder<Set<Catalog>>();
                        extensionManager.retrieveCatalogs(currentSite, catalogResult);
                        if (!CollectionUtils.isEmpty(catalogResult.getResult())) {
                            catalogs.addAll(catalogResult.getResult());
                        }
                    }
                }
                if (catalogs.size() > 0) {
                    catalog = catalogs.get(0);
                }
            }

            if (catalog != null) {
                if (BLCRequestUtils.isOKtoUseSession(request)) {
                    request.setAttribute(CATALOG_REQ_PARAM, catalog.getId(), WebRequest.SCOPE_SESSION);
                }
                brc.setCurrentCatalog(catalog);
            }
            if (extensionManager != null) {
                if (brc.getNonPersistentSite() != null) {
                    Site currentSite = siteService.retrievePersistentSiteById(brc.getNonPersistentSite().getId());
                    ExtensionResultHolder<Catalog> catalogResult = new ExtensionResultHolder<Catalog>();
                    extensionManager.overrideCurrentCatalog(request, currentSite, catalogResult);
                    if (catalogResult.getResult() != null) {
                        brc.setCurrentCatalog(catalogResult.getResult());
                    }

                    ExtensionResultHolder<Site> profileResult = new ExtensionResultHolder<Site>();
                    extensionManager.overrideCurrentProfile(request, currentSite, profileResult);
                    if (profileResult.getResult() != null) {
                        brc.setCurrentProfile(profileResult.getResult());
                    }
                }
            }
        }
    }

    protected void prepareSandBox(WebRequest request, BroadleafRequestContext brc) {
        AdminUser adminUser = adminRemoteSecurityService.getPersistentAdminUser();
        if (adminUser == null) {
            //clear any sandbox
            if (BLCRequestUtils.isOKtoUseSession(request)) {
                request.removeAttribute(BroadleafSandBoxResolver.SANDBOX_ID_VAR, WebRequest.SCOPE_SESSION);
            }
        } else {
            SandBox sandBox = null;
            if (StringUtils.isNotBlank(request.getParameter(SANDBOX_REQ_PARAM))) {
                Long sandBoxId = Long.parseLong(request.getParameter(SANDBOX_REQ_PARAM));
                sandBox = sandBoxService.retrieveUserSandBoxForParent(adminUser.getId(), sandBoxId);
                if (sandBox == null) {
                    SandBox approvalOrUserSandBox = sandBoxService.retrieveSandBoxManagementById(sandBoxId);
                    if (approvalOrUserSandBox != null) {
                        if (approvalOrUserSandBox.getSandBoxType().equals(SandBoxType.USER)) {
                            sandBox = approvalOrUserSandBox;
                        } else {
                            sandBox = sandBoxService.createUserSandBox(adminUser.getId(), approvalOrUserSandBox);
                        }
                    }
                }
                if (BLCRequestUtils.isOKtoUseSession(request)) {
                    String token = request.getParameter(staleStateProtectionService.getStateVersionTokenParameter());
                    staleStateProtectionService.compareToken(token);
                    staleStateProtectionService.invalidateState(true);
                }
            }

            if (sandBox == null) {
                Long previouslySetSandBoxId = null;
                if (BLCRequestUtils.isOKtoUseSession(request)) {
                    previouslySetSandBoxId = (Long) request.getAttribute(BroadleafSandBoxResolver.SANDBOX_ID_VAR,
                        WebRequest.SCOPE_SESSION);
                }
                if (previouslySetSandBoxId != null) {
                    sandBox = sandBoxService.retrieveSandBoxManagementById(previouslySetSandBoxId);
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
            Long previouslySetSandBoxId = null;
            if (BLCRequestUtils.isOKtoUseSession(request)) {
                previouslySetSandBoxId = (Long) request.getAttribute(BroadleafSandBoxResolver.SANDBOX_ID_VAR, WebRequest.SCOPE_SESSION);
            }
            if (previouslySetSandBoxId != null && !sandBox.getId().equals(previouslySetSandBoxId)) {
                adminUser.setLastUsedSandBoxId(sandBox.getId());
                adminUser = adminSecurityService.saveAdminUser(adminUser);
            }

            if (BLCRequestUtils.isOKtoUseSession(request)) {
                request.setAttribute(BroadleafSandBoxResolver.SANDBOX_ID_VAR, sandBox.getId(), WebRequest.SCOPE_SESSION);
            }
            //We do this to prevent lazy init exceptions when this context/sandbox combination
            // is used in a different session that it was initiated in. see QA#2576
            if(sandBox != null && sandBox.getChildSandBoxes() != null) {
                sandBox.getChildSandBoxes().size();
            }
            brc.setSandBox(sandBox);
            brc.setDeployBehavior(deployBehaviorUtil.isProductionSandBoxMode() ? DeployBehavior.CLONE_PARENT : DeployBehavior.OVERWRITE_PARENT);
            brc.getAdditionalProperties().put("adminUser", adminUser);
        }
    }

    @Override
    public void postProcess(WebRequest request) {
        ThreadLocalManager.remove();
    }

}
