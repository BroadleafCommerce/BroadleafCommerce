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
package org.broadleafcommerce.openadmin.server.security.service.navigation;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Jeff Fischer
 */
@Component("blPolymorphicEntityCheckSectionAuthorization")
public class PolymorphicEntitySectionAuthorizationImpl implements SectionAuthorization {

    @Resource(name="blDynamicEntityDao")
    protected DynamicEntityDao dynamicEntityDao;

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @PostConstruct
    public void init() {
        dynamicEntityDao.setStandardEntityManager(em);
    }

    @Override
    public boolean isUserAuthorizedToViewSection(AdminUser adminUser, AdminSection section) {
        if (StringUtils.isBlank(section.getCeilingEntity())) {
            return true;
        }

        try {
            //Only display this section if there are 1 or more entities relative to the ceiling 
            //for this section that are qualified to be created by the admin
            return dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(
                    Class.forName(section.getCeilingEntity()), false).length > 0;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
