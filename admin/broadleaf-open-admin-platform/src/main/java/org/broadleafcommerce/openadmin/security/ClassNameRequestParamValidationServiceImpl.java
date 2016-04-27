/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.security;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelper;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.exception.SectionKeyValidationException;
import org.broadleafcommerce.openadmin.server.security.service.navigation.AdminNavigationService;
import org.hibernate.Session;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @see ClassNameRequestParamValidationService
 * @author Jeff Fischer
 */
@Service("blClassNameRequestParamValidationService")
public class ClassNameRequestParamValidationServiceImpl implements ClassNameRequestParamValidationService, ApplicationListener<ContextRefreshedEvent> {

    private static final Log LOG = LogFactory.getLog(ClassNameRequestParamValidationServiceImpl.class);

    @Resource(name="entityManagerFactory")
    protected EntityManagerFactory factory;

    @Resource(name="blAdminNavigationService")
    protected AdminNavigationService adminNavigationService;

    protected DynamicDaoHelper helper = new DynamicDaoHelperImpl();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        EntityManager em = factory.createEntityManager();
        if (em != null) {
            helper.initializeEntityWhiteList(em.unwrap(Session.class).getSessionFactory(), "blPU");
        } else {
            throw new RuntimeException("Unable to initialize the entity classname whitelist");
        }
    }

    @Override
    public boolean validateClassNameParams(Map<String, String> requestParamToClassName, String persistenceUnitName) {
        boolean validated = false;
        for (Map.Entry<String, String> entry : requestParamToClassName.entrySet()) {
            validated = StringUtils.isEmpty(entry.getValue());
            if (!validated) {
                validated = helper.validateEntityClassName(entry.getValue(), persistenceUnitName);
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
    public String getClassNameForSection(String sectionKey, String persistenceUnitName) {
        String className = adminNavigationService.getClassNameForSection(sectionKey);
        if (sectionKey.equals(className) && !helper.validateEntityClassName(className, persistenceUnitName)) {
            LOG.warn("Non-whitelist section key specified in request URI. Rejecting.");
            throw new SectionKeyValidationException("Non-whitelist section key specified. Rejecting.");
        }
        return className;
    }

    @Override
    public List<SectionCrumb> getSectionCrumbs(String crumbList, String persistenceUnitName) {
        List<SectionCrumb> crumbs = adminNavigationService.getSectionCrumbs(crumbList);
        if (!crumbs.isEmpty()) {
            for (SectionCrumb crumb : crumbs) {
                String test = adminNavigationService.getClassNameForSection(crumb.getSectionIdentifier());
                if (crumb.getSectionIdentifier().equals(test)) {
                    if (!helper.validateEntityClassName(test, persistenceUnitName)) {
                        LOG.warn("Non-whitelist section key specified in sectionCrumbs param. Rejecting.");
                        throw new SectionKeyValidationException("Non-whitelist section key specified. Rejecting.");
                    }
                }
            }
        }
        return crumbs;
    }

}
