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
package org.broadleafcommerce.openadmin.server.security.service;

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
