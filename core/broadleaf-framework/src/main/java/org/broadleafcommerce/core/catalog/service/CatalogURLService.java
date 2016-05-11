/*
 * #%L
 * BroadleafCommerce Framework
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
