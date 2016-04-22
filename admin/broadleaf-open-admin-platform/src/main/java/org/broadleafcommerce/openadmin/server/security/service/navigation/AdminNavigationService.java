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
