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
package org.broadleafcommerce.openadmin.server.security.service.user;

import org.broadleafcommerce.common.security.BroadleafExternalAuthenticationUserDetails;

/**
 * Utility to add or update the AdminUser object in the database after authentication from an external system.
 * 
 * @author Kelly Tisdell
 *
 */
public interface AdminUserProvisioningService {

    /**
     * This method uses the details argument to add or update an AdminUser object in the database, 
     * along with appropriate roles and permissions.  The result of the call to this should be an instance of 
     * AdminUserDetails.
     * 
     * NOTE: IT IS GENERALLY EXPECTED THAT THIS METHOD WILL BE CALLED AFTER A USER HAS BEEN AUTHENTICATED.
     * 
     * @param details
     * @return
     */
    public AdminUserDetails provisionAdminUser(BroadleafExternalAuthenticationUserDetails details);

}
