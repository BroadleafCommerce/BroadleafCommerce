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

import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTO;
import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbService;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * This service generates metadata for pages that are not specialized. It includes the organization, website, and
 * breadcrumb list.
 *
 * @author Jacob Mitash
 */
@Service(value = "blDefaultLinkedDataServiceImpl")
public class DefaultLinkedDataServiceImpl implements LinkedDataService {

    protected final static String DEFAULT_CONTEXT = "http://schema.org/";

    @Autowired
    protected Environment environment;

    @Autowired
    protected BreadcrumbService breadcrumbService;


    @Override
    public Boolean canHandle(LinkedDataDestinationType destination) {
        return LinkedDataDestinationType.DEFAULT.equals(destination);
    }

    @Override
    public String getLinkedData(String url, List<Product> products) throws JSONException {
        return getLinkedDataJson(url, products).toString();
    }

    protected JSONArray getLinkedDataJson(String url, List<Product> products) throws JSONException {
        JSONArray schemaObjects = new JSONArray();

        schemaObjects.put(getBreadcrumbList());

        return schemaObjects;
    }

    /**
     * Generates an object representing the Schema.org BreadcrumbList
     *
     * @return JSON representation of BreadcrumbList from Schema.org
     */
    protected JSONObject getBreadcrumbList() throws JSONException {
        JSONObject breadcrumbObjects = new JSONObject();

        breadcrumbObjects.put("@context", DEFAULT_CONTEXT);
        breadcrumbObjects.put("@type", "BreadcrumbList");

        String requestUri = getRequestUri();
        Map<String, String[]> params = getRequestParams();

        List<BreadcrumbDTO> breadcrumbs = breadcrumbService.buildBreadcrumbDTOs(requestUri, params);

        int index = 1;
        JSONArray breadcrumbList = new JSONArray();
        for(BreadcrumbDTO breadcrumb : breadcrumbs) {
            JSONObject listItem = new JSONObject();
            listItem.put("@type", "ListItem");
            listItem.put("position", index);

            JSONObject item = new JSONObject();
            item.put("@id", getSiteBaseUrl() + breadcrumb.getLink());
            item.put("name", breadcrumb.getText());

            listItem.put("item", item);
            breadcrumbList.put(listItem);
            index++;
        }

        breadcrumbObjects.put("itemListElement", breadcrumbList);

        return breadcrumbObjects;
    }

    protected String getRequestUri() {
        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();

        return request.getRequestURI();
    }

    protected static Map<String, String[]> getRequestParams() {
        Map<String, String[]> params = new HashMap<>();

        if(BroadleafRequestContext.getRequestParameterMap() != null) {
            params = new HashMap<>(BroadleafRequestContext.getRequestParameterMap());
        }

        return params;
    }

    protected String getSiteBaseUrl() {
        return environment.getProperty("site.baseurl");
    }

}
