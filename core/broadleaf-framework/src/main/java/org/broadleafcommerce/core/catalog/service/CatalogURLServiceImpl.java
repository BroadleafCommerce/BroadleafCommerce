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

import org.apache.http.client.utils.URIBuilder;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;

import java.net.URI;
import java.net.URISyntaxException;

@Service("blCatalogURLService")
public class CatalogURLServiceImpl implements CatalogURLService {

    @Value("${catalogUriService.appendIdToRelativeURI:true}")
    protected boolean appendIdToRelativeURI;

    @Value("${catalogUriService.useUrlKey:false}")
    protected boolean useUrlKey;

    @Value("${catalogUriService.productIdParam:productId}")
    protected String productIdParam;

    @Value("${catalogUriService.categoryIdParam:categoryId}")
    protected String categoryIdParam;

    @Override
    public String buildRelativeProductURL(String currentUrl, Product product) {
        String fragment = getProductUrlFragment(product);
        return buildRelativeUrlWithParam(currentUrl, fragment, productIdParam, String.valueOf(product.getId()));
    }

    @Override
    public String buildRelativeCategoryURL(String currentUrl, Category category) {
        String fragment = getCategoryUrlFragment(category);
        return buildRelativeUrlWithParam(currentUrl, fragment, categoryIdParam, String.valueOf(category.getId()));
    }

    /**
     * Adds the fragment to the end of the path and optionally adds an id param depending upon
     * the value of appendIdToRelativeURI.
     */
    protected String buildRelativeUrlWithParam(String currentUrl, String fragment, String idParam, String idValue) {
        try {
            URIBuilder builder = new URIBuilder(currentUrl);
            builder.setPath(builder.getPath() + "/" + fragment);

            if (appendIdToRelativeURI) {
                builder.setParameter(idParam, String.valueOf(idValue));
            }

            return builder.build().toString();
        } catch (URISyntaxException e) {
            return currentUrl;
        }
    }

    protected String getProductUrlFragment(Product product) {
        if (useUrlKey) {
            return product.getUrlKey();
        } else {
            return getLastFragment(product.getUrl());
        }
    }

    protected String getCategoryUrlFragment(Category category) {
        if (useUrlKey) {
            return category.getUrlKey();
        } else {
            return getLastFragment(category.getUrl());
        }
    }

    protected String getLastFragment(String url) {
        URI uri = URI.create(url);
        String path = Optional.fromNullable(uri.getPath()).or("/");
        return path.substring(path.lastIndexOf('/') + 1);
    }
}
