/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.common.crossapp.service;

import java.util.List;

/**
 * A service responsible for allowing secure authentication for a user between the admin and site applications.
 * 
 * This service generates a single use and time sensitive token for a user from the admin application. This token is sent
 * to the user and he must present it in a timely manner to the site application to associate his session as authenticated
 * from the admin applicaiton.
 * 
 * @see CrossAppAuthService
 * @author Andre Azzolini (apazzolini)
 */
public interface CrossAppAdminAuthService {

    /**
     * Composes a full URL that can be returned from a controller to redirect the user to the cross app authentication
     * controller endpoint on the site application.
     * 
     * @param forwardUrl (not URL encoded)
     * @param rolesToContrib
     * @return the redirect url
     */
    public String getRedirectUrlForSiteAuth(String forwardUrl, List<String> rolesToContrib);

    /**
     * @see #generateTokenForSiteAuth(Long, List)
     * @param adminUserId
     * @return the generated token
     */
    public String generateTokenForSiteAuth(Long adminUserId);

    /**
     * Returns a randomly generated String that the user can then include in a request from the site application to
     * associate his site session with an admin user.
     * 
     * If the rolesToContrib parameter is not null, the roles in that list will be added to the site user when the
     * token is claimed.
     * 
     * @param adminUserId
     * @param rolesToContrib
     * @return
     */
    public String generateTokenForSiteAuth(Long adminUserId, List<String> rolesToContrib);

}