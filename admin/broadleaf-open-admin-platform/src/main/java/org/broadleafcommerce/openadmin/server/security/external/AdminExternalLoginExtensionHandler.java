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
package org.broadleafcommerce.openadmin.server.security.external;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.security.BroadleafExternalAuthenticationUserDetails;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;

public interface AdminExternalLoginExtensionHandler extends ExtensionHandler {

    /**
     * This can be used to associate, for example, Site to the adminUser, or to validate that the Site that the adminUser 
     * has access to is the current site.  Implementors may also wish to assign additional data to the admin user, persist 
     * custom data, validate additional access rules, etc.
     * Implementors should not persist the adminUser object. Rather modify or augment the state of the adminUser object 
     * only.  Persistence should be handled outside of this.
     * 
     * If an implementor decides that the user is not actually authenticated or should not be allowed access, an instance of  
     * <code>org.springframework.security.core.AuthenticationException</code> should be thrown.
     * 
     * @param adminUser
     * @param details
     * @return
     */
    public ExtensionResultStatusType performAdditionalAuthenticationTasks(
            AdminUser adminUser, BroadleafExternalAuthenticationUserDetails details);
    

}
