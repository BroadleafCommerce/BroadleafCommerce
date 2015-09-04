/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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

package org.broadleafcommerce.core.web.breadcrumbs;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTO;
import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTOType;
import org.broadleafcommerce.common.breadcrumbs.service.AbstractBreadcrumbServiceExtensionHandler;
import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbHandlerDefaultPriorities;
import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbServiceExtensionManager;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service("blCategoryBreadcrumbServiceExtensionHandler")
public class CategoryBreadcrumbServiceExtensionHandler extends AbstractBreadcrumbServiceExtensionHandler {

    private static final Log LOG = LogFactory.getLog(CategoryBreadcrumbServiceExtensionHandler.class);

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blBreadcrumbServiceExtensionManager")
    protected BreadcrumbServiceExtensionManager extensionManager;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    public ExtensionResultStatusType modifyBreadcrumbList(String url, Map<String, String[]> params,
            ExtensionResultHolder<List<BreadcrumbDTO>> holder) {

        // ProductBreadcrumb Handler strips off the productId and last fragment  
        String testUrl = (String) holder.getContextMap().get(BreadcrumbServiceExtensionManager.CONTEXT_PARAM_STRIPPED_URL);
        if (StringUtils.isEmpty(testUrl)) {
            testUrl = url;
        }

        Category category = determineFirstCategory(testUrl, params, holder);

        if (category != null) {
            BreadcrumbDTO categoryCrumb = buildCrumbForCategory(category, url, params);
            BreadcrumbDTO categorySearchCrumb = buildCategorySearchCrumb(category, url, params);

            List<BreadcrumbDTO> parentCrumbs = new ArrayList<BreadcrumbDTO>();
            addParentCrumbs(parentCrumbs, category, url, params);

            if (categorySearchCrumb != null) {
                holder.getResult().add(0, categorySearchCrumb);
            }

            if (categoryCrumb != null) {
                holder.getResult().add(0, categoryCrumb);
            }

            if (!parentCrumbs.isEmpty()) {
                holder.getResult().addAll(0, parentCrumbs);
            }
        }

        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }

    protected BreadcrumbDTO buildCrumbForCategory(Category category, String url, Map<String, String[]> params) {
        BreadcrumbDTO categoryDto = null;
        if (category != null) {
            categoryDto = new BreadcrumbDTO();
            categoryDto.setText(getNameForCategoryLink(category));
            categoryDto.setLink(category.getUrl());
            categoryDto.setType(BreadcrumbDTOType.CATEGORY);
        }
        return categoryDto;
    }

    /**
     * Add the parent crumb for the passed in category.    
     * Recursively call to find all parents.
     * 
     * @param parentCrumbs
     * @param category
     * @param url
     * @param params
     */
    protected void addParentCrumbs(List<BreadcrumbDTO> parentCrumbs, Category category, String url,
            Map<String, String[]> params) {

        Category parentCategory = category.getParentCategory();
        if (parentCategory != null && !parentCrumbs.contains(parentCategory)) { // prevent recursion
            BreadcrumbDTO dto = buildCrumbForCategory(parentCategory, url, params);
            parentCrumbs.add(0, dto);
            addParentCrumbs(parentCrumbs, parentCategory, url, params);
        }
    }

    /**
     * Hook for overrides, some implementations may want to build a crumb related to category filtering. 
     * 
     * Out of box does nothing here.
     * 
     * @param category
     * @param url
     * @param params
     * @return
     */
    protected BreadcrumbDTO buildCategorySearchCrumb(Category category, String url, Map<String, String[]> params) {
        return null;
    }

    protected String getNameForCategoryLink(Category category) {
        return category.getName();
    }

    protected Category determineFirstCategory(String testUrl, Map<String, String[]> params,
            ExtensionResultHolder<List<BreadcrumbDTO>> holder) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        Category returnCategory = null;

        if (brc != null && brc.getRequest() != null) {
            if (returnCategory == null) {
                returnCategory = getMatchingCategoryFromProduct(brc, testUrl, params);
            }

            if (returnCategory == null) {
                returnCategory = getCategoryFromCategoryAttribute(brc, testUrl, params);
            }

            if (returnCategory == null) {
                returnCategory = getCategoryFromUrl(brc, testUrl, params);
            }
        }
        return returnCategory;
    }

    /**
     * This is an efficient test that checks to see if the requestURI matches the product URI.
     * This works well for sites whose category urls prefix their product urls.
     * 
     * @param brc
     * @param testUrl
     * @param params
     * @return
     */
    protected Category getMatchingCategoryFromProduct(BroadleafRequestContext brc, String testUrl,
            Map<String, String[]> params) {
        if (brc != null) {
            Product product = (Product) brc.getRequestAttribute("currentProduct"); // see ProductHandlerMapping
            if (product != null) {
                Category testCategory = product.getCategory();
                if (testCategory != null && testUrl != null) {
                    if (testUrl.equals(testCategory.getUrl())) {
                        return testCategory;
                    }
                }
            }
        }
        return null;
    }

    /**
     * This indicates that we are on a category page
     * @param brc
     * @param testUrl
     * @param params
     * @return
     */
    protected Category getCategoryFromCategoryAttribute(BroadleafRequestContext brc, String testUrl,
            Map<String, String[]> params) {
        return (Category) brc.getRequestAttribute("category");
    }

    protected Category getCategoryFromUrl(BroadleafRequestContext brc, String requestUrl,
            Map<String, String[]> params) {

        return catalogService.findCategoryByURI(requestUrl);
    }

    @Override
    public int getDefaultPriority() {
        return BreadcrumbHandlerDefaultPriorities.CATEGORY_CRUMB;
    }
}
