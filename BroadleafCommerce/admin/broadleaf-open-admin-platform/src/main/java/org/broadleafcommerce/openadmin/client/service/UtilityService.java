/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.openadmin.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.gwtincubator.security.exception.ApplicationSecurityException;

/**
 * {@code UtilityService} provides several basic function to the admin revolving
 * around retrieving current context information for the admin app.
 *
 * NOTE - this service is NOT secured. Do not put features in this service
 * interface that require security!
 *
 * @author jfischer
 *
 */
public interface UtilityService extends RemoteService {

    /**
     * Retrieve the current web application context (if any) in use for this admin application. When
     * a web app context is in use, it appears as the first part of the url. For example, in the url
     * http://localhost:8080/broadleafdemo/admin.html, the web app context is 'broadleafdemo'. Null
     * may be returned if there is no app context.
     *
     * @return The first part of the app url that constitutes the web application context (if any).
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
	public String getWebAppContext() throws ServiceException, ApplicationSecurityException;

    /**
     * Retrieve the current web application context (if any) for the actual store front associated
     * with this admin instance. For example, if the storefront is http://localhost:8080/myStore, then
     * the store front web app context is 'myStore'. Null may be returned if there is no web app context
     * set for the storefront.
     *
     * @return The first part of the storefront url that constitutes the web application context (if any)
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
	public String getStoreFrontWebAppContext() throws ServiceException, ApplicationSecurityException;
    
}
