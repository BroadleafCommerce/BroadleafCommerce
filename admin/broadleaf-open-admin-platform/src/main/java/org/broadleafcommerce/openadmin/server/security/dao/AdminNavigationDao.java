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
package org.broadleafcommerce.openadmin.server.security.dao;


import org.broadleafcommerce.openadmin.server.security.domain.AdminModule;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;

import java.util.List;

/**
 *
 * @author elbertbautista
 *
 */
public interface AdminNavigationDao {

    public List<AdminModule> readAllAdminModules();

    public List<AdminSection> readAllAdminSections();
    
    public AdminSection readAdminSectionByClassAndSectionId(Class<?> clazz, String sectionId);

    public AdminSection readAdminSectionByURI(String uri);

    public AdminSection readAdminSectionBySectionKey(String sectionKey);

    public AdminSection save(AdminSection adminSection);

    public void remove(AdminSection adminSection);

    public AdminModule readAdminModuleByModuleKey(String moduleKey);

    String getSectionKey(boolean withTypeKey);
}
