/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.security.service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * This service is responsible for monitoring key changes in state for a given session. If it is detected that a key state
 * change has taken place after the current page was rendered, but before the form on the page is submitted, the service
 * will throw a {@link StaleStateServiceException}, which will result in a Http Status code 409 error response.
 * </p>
 * Examples of key state changes in the admin include:
 * <ul>
 *     <li>Site switching</li>
 *     <li>Sandbox switching</li>
 *     <li>Catalog switching</li>
 *     <li>profile switching</li>
 * </ul>
 * </p>
 * It is important to detect these state changes and prevent stale data submissions in order to prevent data corruption.
 * Such corruption can include entities being associated with incorrect or unintended sites, sandboxes, catalogs and profiles.
 * </p>
 * The classic use case that can expose the problem is to use multiple browser tabs for the admin - all using the same
 * session. Such a configuration can allow a user to expose a product detail form (for example) in one browser tab. Then
 * in a separate browser tab, the user can perform a state switch. Upon returning to the original tab and submitting the
 * form, the data can be persisted in unanticipated ways as it relates to site, sandbox, catalog or profile.
 * </p>
 * This service is disabled by default to guarantee backwards compatibility. However, enabling the service for any installation
 * is relatively easy. First, a property must be seet in the application's property files:
 * </p>
 * {@code stale.state.protection.enabled = true}
 * </p>
 * Second, the applicationContext-admin-security.xml file should be checked for the appropriate filter configuration:
 * </p>
 * {@code
 *      ...
 *       <sec:custom-filter ref="blPreSecurityFilterChain" before="CHANNEL_FILTER"/>
 *        <sec:custom-filter ref="blSecurityFilter" before="FORM_LOGIN_FILTER"/>
 *        <sec:custom-filter ref="blAdminFilterSecurityInterceptor" after="EXCEPTION_TRANSLATION_FILTER"/>
 *        <sec:custom-filter ref="blPostSecurityFilterChain" after="SWITCH_USER_FILTER"/>
 *    </sec:http>
 *   <bean id="blSecurityFilter" class="org.broadleafcommerce.openadmin.web.filter.AdminSecurityFilter" />
 *   ...
 * }
 * </p>
 * Specifically, the "blSecurityFilter" bean and its custom-filter reference config are key here. Note, if present,
 * the "blCsrfFilter" should be replaced with this config, as "blSecurityFilter" encompasses both CSRF protection, as well
 * as the stale state protection described here.
 * </p>
 * Finally, the mechanism of protection is provided by utilizing a state version token. The token is included in the body
 * of each page and represents the current session state for the user. When a key state change event takes place, the state
 * version token is updated in the user session on the server. When the system detects a mismatch between the token provided
 * by a request and the token that exists in the session, the system will emit a {@link StaleStateServiceException}.
 *
 * @author Jeff Fischer
 */
public interface StaleStateProtectionService {

    /**
     * Compare the state version token provided by the request to what is resident in the user session. If the tokens
     * are not equal, the determination is made that the request is coming from a stale page and a {@link StaleStateServiceException}
     * is emitted.
     *
     * @param passedToken the state version token from the request
     */
    void compareToken(String passedToken);

    /**
     * Retrieve the state version token resident in the user's session, or create one if it doesn't exist.
     *
     * @return
     */
    String getStateVersionToken();

    /**
     * Remove the current state version token in the user's session, if exists. This usually occurs in response to a key
     * state change.
     */
    void invalidateState();

    /**
     * Remove the current state version token in the user's session, if exists. This usually occurs in response to a key
     * state change.
     *
     * @param notify Whether or not the request should be notified that state was invalidated. See {@link #sendRedirectOnStateChange(HttpServletResponse, String...)}.
     */
    void invalidateState(boolean notify);

    /**
     * Whether or not the protection provided by this service is active.
     *
     * @return
     */
    boolean isEnabled();

    /**
     * Retrieve the parameter key used to harvest the state version token value from the request
     *
     * @return
     */
    String getStateVersionTokenParameter();

    /**
     * Utility method to send a redirect for the current url with state change params cleared. State change params are request
     * url parameters that drive primary context change. For example, url parameters for sandbox, catalog and profile changes
     * are considered state change params. When such a param is utilized, the state token is reset to represent the fundamental
     * context change and is stored in session. However, it is now desirable to redirect the user to the target url without
     * the state change params to avoid a subsequent check.
     *
     * @param response the current http response
     * @param stateChangeParams any request parameter names being used by the system to drive state change
     * @return whether or not a redirect was sent based on detection of an earlier call to {@link #invalidateState()}
     * @throws IOException
     */
    boolean sendRedirectOnStateChange(HttpServletResponse response, String... stateChangeParams) throws IOException;

}
