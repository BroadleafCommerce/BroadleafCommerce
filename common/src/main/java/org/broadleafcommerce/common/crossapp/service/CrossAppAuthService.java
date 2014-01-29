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

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * A service responsible for allowing secure authentication for a user between the admin and site applications.
 * 
 * This service generates a single use and time sensitive token for a user from the admin application. This token is sent
 * to the user and he must present it in a timely manner to the site application to associate his session as authenticated
 * from the admin applicaiton.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface CrossAppAuthService {

    public static String AUTH_FROM_ADMIN_URL_PARAM = "blAuthToken";
    public static String AUTH_FROM_ADMIN_SESSION_VAR = "blAuthedFromAdmin";

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

    /**
     * Consumes an authentication token for the given user id and token. This method will additionally register the
     * current session (acquired from the {@link RedirectAttributes} argument) as having an admin authentication for the
     * given adminUserId.
     * 
     * @param adminUserId
     * @param token
     * @param ra
     * @throws IllegalArgumentException
     */
    public void useSiteAuthToken(Long adminUserId, String token) throws IllegalArgumentException;

    /**
     * @return whether or not the user is currently authenticated from the admin
     */
    public boolean isAuthedFromAdmin();

    /**
     * @return the id of the currently authenticated admin user. Returns null if there is no currently authenticated user
     */
    public Long getCurrentAuthedAdminId();

}