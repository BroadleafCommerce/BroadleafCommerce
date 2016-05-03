/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;

/**
 * This service provides some URL manipulation capabilities.   Initially provided to support the creation of
 * relative URLs and Breadcrumb requirements.    
 * 
 * @author bpolster
 * @see org.broadleafcommerce.core.web.processor.CatalogRelativeHrefProcessor
 */
public interface CatalogURLService {

    /**
     * Provides relative URLs.     This is useful for cases where a site wants to 
     * build a dynamic URL to get to a product or category where multiple navigation paths
     * are provided.
     * 
     * For example, consider a product with URL (/equipment/tennis-ball) that is in two categories 
     * which have the following URLs (/sports and /specials). 
     * 
     * For some implementations, it is desirable to have two semantic URLs such as 
     * "/sports/tennis-ball" and "/specials/tennis-ball".
     * 
     * This method will take the last fragment of the product URL and append it to the 
     * passed in URL to make a relative URL.
     * 
     * This default implementation of this interface uses two system properties to control 
     * its behavior.
     * 
     * catalogUriService.appendIdToRelativeURI - If true (default), a query param will be appended to the URL
     * with the productId.
     * 
     * catalogUriService.useUrlKey - If true (default is false), the implementation will call the
     * ProductImpl.getUrlKey() to obtain the url fragment.   If false, it will parse the last part of the 
     * ProductImpl.getUrl(). 
     * 
     * Returns the URL as a string including query parameters.
     * 
     * @param currentUrl
     * @param product
     * @return
     */
    String buildRelativeProductURL(String currentUrl, Product product);

    /**
     * See similar description for {@link #buildRelativeProductURL(String, Product)}
     * @param currentUrl
     * @param category
     * @return
     */
    String buildRelativeCategoryURL(String currentUrl, Category category);
}
