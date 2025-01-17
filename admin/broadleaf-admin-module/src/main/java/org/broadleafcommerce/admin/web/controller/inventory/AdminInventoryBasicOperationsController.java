/*-
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.admin.web.controller.inventory;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicOperationsController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller("blAdminInventoryBasicOperationsController")
@RequestMapping("/com.broadleafcommerce.inventory.advanced.domain.InventoryImpl")
public class AdminInventoryBasicOperationsController extends AdminBasicOperationsController {

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @RequestMapping(value = "/{collectionField:.*}/select", method = RequestMethod.GET)
    public String showSelectCollectionItem(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @PathVariable Map<String, String> pathVars,
            @PathVariable String collectionField,
            @RequestParam(required = false) String requestingEntityId,
            @RequestParam(required = false) boolean dynamicField,
            @RequestParam MultiValueMap<String, String> requestParams
    ) throws Exception {
        String view = super.showSelectCollectionItem(
                request, response, model, pathVars, "com.broadleafcommerce.inventory.advanced.domain.InventoryImpl",
                collectionField, requestingEntityId, dynamicField, requestParams
        );
        String queryString = requestParams.entrySet().stream()
                .flatMap(k -> k.getValue().stream()
                        .map(v -> new AbstractMap.SimpleEntry<>(k.getKey(), v))
                )
                .map(e -> e.getKey() + '=' + e.getValue())
                .collect(Collectors.joining("&"));
        if (!queryString.contains("fulfillmentType=") && StringUtils.isNotEmpty(request.getParameter("inventoryParameter"))) {
            List<SectionCrumb> sectionCrumbs = this.getSectionCrumbs(request, null, null);
            if (CollectionUtils.isNotEmpty(sectionCrumbs) && sectionCrumbs.get(0).getSectionIdentifier().equals("inventory")) {
                FulfillmentType defaultFulfillmentType = getDefaultFulfillmentType(sectionCrumbs.get(0));
                if (queryString.length() > 0 && defaultFulfillmentType != null) {
                    queryString += "&fulfillmentType=" + defaultFulfillmentType.getType();
                }
            }
        }
        model.addAttribute("queryParamsForFilters", queryString);
        return view;
    }

    @Override
    protected void modifyFetchPersistencePackageRequest(PersistencePackageRequest ppr, Map<String, String> pathVars) {
        super.modifyFetchPersistencePackageRequest(ppr, pathVars);
        Optional<FilterAndSortCriteria> fType = Arrays.stream(ppr.getFilterAndSortCriteria())
                .filter(f -> f.getPropertyId().equals("fulfillmentType"))
                .findFirst();
        Optional<FilterAndSortCriteria> inventoryParameter = Arrays.stream(ppr.getFilterAndSortCriteria())
                .filter(t -> t.getPropertyId().equals("inventoryParameter"))
                .findFirst();

        if (!fType.isPresent() && inventoryParameter.isPresent()) {
            SectionCrumb[] sectionCrumbs = ppr.getSectionCrumbs();
            if (sectionCrumbs.length > 0) {
                SectionCrumb sectionCrumb = sectionCrumbs[0];
                if (sectionCrumb.getSectionIdentifier().equals("inventory")) {
                    FulfillmentType fulfillmentType = getDefaultFulfillmentType(sectionCrumb);
                    if (fulfillmentType != null) {
                        FilterAndSortCriteria fasCriteria = new FilterAndSortCriteria(
                                "fulfillmentType", fulfillmentType.getType()
                        );
                        ppr.addFilterAndSortCriteria(fasCriteria);
                    }
                }
            }
        }
    }

    protected FulfillmentType getDefaultFulfillmentType(SectionCrumb sectionCrumb) {
        String sectionId = sectionCrumb.getSectionId();
        Sku sku = catalogService.findSkuById(Long.valueOf(sectionId));
        return sku.getFulfillmentType();
    }

}
