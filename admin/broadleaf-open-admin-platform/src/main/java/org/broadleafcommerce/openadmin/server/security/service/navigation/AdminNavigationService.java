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

import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.security.domain.AdminMenu;
import org.broadleafcommerce.openadmin.server.security.domain.AdminModule;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;

import java.util.List;
import java.util.Set;

public interface AdminNavigationService {

    public AdminMenu buildMenu(AdminUser adminUser);

    public boolean isUserAuthorizedToViewSection(AdminUser adminUser, AdminSection section);

    public boolean isUserAuthorizedToViewModule(AdminUser adminUser, AdminModule module);

    public AdminSection findAdminSectionByURI(String uri);

    public AdminSection findAdminSectionBySectionKey(String sectionKey);

    /**
     * In some cases, a single class is served by more than one section.
     * 
     * @param className
     * @param sectionId
     * @return
     */
    AdminSection findAdminSectionByClassAndSectionId(String className, String sectionId);

    /**
     * In some cases, a single class is served by more than one section.
     * 
     * @param className
     * @param sectionId
     * @return
     */
    AdminSection findAdminSectionByClassAndSectionId(Class<?> clazz, String sectionId);

    /**
     * Gets all of the {@link AdminSection}s in the system, sorted by the {@link AdminSection#getDisplayOrder()}
     * @return the list of all {@link AdminSection}s sorted by {@link AdminSection#getDisplayOrder()}
     */
    public List<AdminSection> findAllAdminSections();

    public AdminSection save(AdminSection adminSection);

    public void remove(AdminSection adminSection);

    boolean checkPermissions(Set<String> authorizedPermissionNames, String permissionName);

    /**
     * Gets the fully qualified ceiling entity classname for this section. If this section is not explicitly defined in
     * the database, will return the value passed into this function. For example, if there is a mapping from "/myentity" to
     * "com.mycompany.myentity", both "http://localhost/myentity" and "http://localhost/com.mycompany.myentity" are valid
     * request paths.
     *
     * @param sectionKey
     * @return the className for this sectionKey if found in the database or the sectionKey if not
     */
    String getClassNameForSection(String sectionKey);

    /**
     * Utility method for parsing a delimitted section crumb list (usually the "sectionCrumbs" parameter on the HttpServletRequest). Should
     * return a list of {@link SectionCrumb} instances used to identify the different, currently active admin sections.
     *
     * @param crumbList delimitted string of section identifiers
     * @return currently active admin sections in the order specified in the crumbList param
     */
    List<SectionCrumb> getSectionCrumbs(String crumbList);
}
