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
 * {@code UtilityService} provides several basic functions to the admin revolving
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
     * Retrieve the current web url prefix (if any) for the actual store front associated
     * with this admin instance. This value is used by the admin to enable preview features for
     * images and the like that are hosted in the store application. For example, if the admin application
     * is located at http://localhost:8080/admin/admin.html:
     *
     * 1) If the store is located at http://localhost:8080/mystore - The store front web app prefix could
     * be /mystore, since it's located on the same server as the admin
     *
     * 2) If the store is located at http://anotherserver:8080/mystore - The store front web app prefix would
     * be http://anotherserver:8080/mystore, since it's located on another server and the full url is required.
     *
     * This value may be null if the admin app and the store front app are the same.
     *
     * @return The first part of the storefront url that constitutes the web application context (if any)
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
	public String getStoreFrontWebAppPrefix() throws ServiceException, ApplicationSecurityException;

    /**
     * Retrieve the portion of the url that is key for identifying a request for a CMS managed asset.
     * For example, the URL http://localhost:8080/mystore/cms/staticasset/productImage1.jpg is
     * requesting a static asset (productImage1.jpg) if the assetServerUrlPrefix is set to 'cms/staticasset'.
     *
     * The default value is 'cms/staticasset'
     *
     * @return The key portion of the url that identifies CMS managed asset requests
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    public String getAssetServerUrlPrefix() throws ServiceException, ApplicationSecurityException;

    /**
     * Retrieve all the context and prefix values called out in the interface as a single array. The values
     * and ordering are as follows:
     *
     * 1) webAppContext
     * 2) storeFrontWebAppPrefix
     * 3) assetServerUrlPrefix
     *
     * @return All the context and prefix values
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    public String[] getAllItems() throws ServiceException, ApplicationSecurityException;

    /**
     * Check if any items are enabled for workflow (catalog, assets, pages, or structured content)
     *
     * @return Whether or not any items are enabled for workflow.
     * @throws org.broadleafcommerce.openadmin.client.service.ServiceException
     * @throws com.gwtincubator.security.exception.ApplicationSecurityException
     */
    public Boolean getWorkflowEnabled() throws ServiceException, ApplicationSecurityException;
    
}
