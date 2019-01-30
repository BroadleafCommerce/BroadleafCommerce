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
package org.broadleafcommerce.openadmin.security;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.service.PersistenceService;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelper;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.exception.SectionKeyValidationException;
import org.broadleafcommerce.openadmin.server.security.service.navigation.AdminNavigationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;

/**
 * @see ClassNameRequestParamValidationService
 * @author Jeff Fischer
 */
@Service("blClassNameRequestParamValidationService")
public class ClassNameRequestParamValidationServiceImpl implements ClassNameRequestParamValidationService {

    private static final Log LOG = LogFactory.getLog(ClassNameRequestParamValidationServiceImpl.class);

    @Resource(name="entityManagerFactory")
    protected EntityManagerFactory factory;

    @Resource(name="blAdminNavigationService")
    protected AdminNavigationService adminNavigationService;

    @Resource(name="blPersistenceService")
    protected PersistenceService persistenceService;

    protected DynamicDaoHelper helper = new DynamicDaoHelperImpl();

    @Override
    public boolean validateClassNameParams(Map<String, String> requestParamToClassName, String persistenceUnitName) {
        boolean validated = false;
        for (Map.Entry<String, String> entry : requestParamToClassName.entrySet()) {
            validated = StringUtils.isEmpty(entry.getValue());
            if (!validated) {
                validated = persistenceService.validateEntityClassName(entry.getValue());
                if (!validated) {
                    LOG.warn(String.format("Non-whitelist %s specified. Rejecting.", entry.getKey()));
                }
            }
            if (!validated) {
                break;
            }
        }
        return validated;
    }

    @Override
    public String getClassNameForSection(String sectionKey) {
        String className = adminNavigationService.getClassNameForSection(sectionKey);
        if (sectionKey.equals(className) && !persistenceService.validateEntityClassName(className)) {
            LOG.warn("Non-whitelist section key specified in request URI. Rejecting.");
            throw new SectionKeyValidationException("Non-whitelist section key specified. Rejecting.");
        }
        return className;
    }

    @Override
    public List<SectionCrumb> getSectionCrumbs(String crumbList) {
        List<SectionCrumb> crumbs = adminNavigationService.getSectionCrumbs(crumbList);
        if (!crumbs.isEmpty()) {
            for (SectionCrumb crumb : crumbs) {
                String test = adminNavigationService.getClassNameForSection(crumb.getSectionIdentifier());
                if (crumb.getSectionIdentifier().equals(test)) {
                    if (!persistenceService.validateEntityClassName(test)) {
                        LOG.warn("Non-whitelist section key specified in sectionCrumbs param. Rejecting.");
                        throw new SectionKeyValidationException("Non-whitelist section key specified. Rejecting.");
                    }
                }
            }
        }
        return crumbs;
    }

}
