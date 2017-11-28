/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.linkeddata.service;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service generates metadata unique to the category pages. It will list all the products in a category for SEO.
 *
 * @author Jacob Mitash
 */
@Service(value = "blCategoryLinkedDataServiceImpl")
public class CategoryLinkedDataServiceImpl extends DefaultLinkedDataServiceImpl {

    @Override
    public Boolean canHandle(LinkedDataDestinationType destination) {
        return LinkedDataDestinationType.CATEGORY.equals(destination);
    }

    @Override
    protected JSONArray getLinkedDataJson(String url, List<Product> products) throws JSONException {
        JSONArray schemaObjects = super.getLinkedDataJson(url, products);

        JSONObject categoryData = new JSONObject();
        categoryData.put("@context", "http://schema.org");
        categoryData.put("@type", "ItemList");
        JSONArray itemList = new JSONArray();
        for(int i = 0; i < products.size(); i++) {
            JSONObject item = new JSONObject();
            item.put("@type", "ListItem");
            item.put("position", i + 1);
            item.put("url", products.get(i).getUrl());
            itemList.put(item);
        }
        categoryData.put("itemListElement", itemList);

        schemaObjects.put(categoryData);
        schemaObjects.put(getBreadcrumbList());

        return schemaObjects;

    }
}
