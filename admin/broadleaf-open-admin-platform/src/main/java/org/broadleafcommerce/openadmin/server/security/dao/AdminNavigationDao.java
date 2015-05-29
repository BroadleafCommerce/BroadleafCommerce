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

}
