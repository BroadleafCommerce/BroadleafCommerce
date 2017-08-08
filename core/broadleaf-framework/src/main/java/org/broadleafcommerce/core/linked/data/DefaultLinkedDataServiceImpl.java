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
package org.broadleafcommerce.core.linked.data;

import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbService;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service generates metadata for pages that are not specialized. It includes the organization, website, and
 * breadcrumb list.
 *
 * @author Jacob Mitash
 */
@Service(value = "blDefaultLinkedDataServiceImpl")
public class DefaultLinkedDataServiceImpl implements LinkedDataService {

    @Autowired
    protected Environment environment;

    @Autowired
    protected BreadcrumbService breadcrumbService;

    @Override
    public Boolean canHandle(LinkedDataDestinationType destination) {
        return LinkedDataDestinationType.DEFAULT.equals(destination);
    }

    protected JSONArray getLinkedDataJson(String url, List<Product> products) throws JSONException {
        JSONArray schemaObjects = new JSONArray();

        schemaObjects.put(LinkedDataUtil.getDefaultOrganization(environment, url));
        schemaObjects.put(LinkedDataUtil.getDefaultWebSite(environment, url));
        if(breadcrumbService != null) {
            schemaObjects.put(LinkedDataUtil.getDefaultBreadcrumbList(breadcrumbService, url));
        }

        return schemaObjects;
    }

    @Override
    public String getLinkedData(String url, List<Product> products) throws JSONException {
        return getLinkedDataJson(url, products).toString();
    }
}
