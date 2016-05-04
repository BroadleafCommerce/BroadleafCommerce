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
