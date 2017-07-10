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

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

/**
 * This service generates metadata for pages that are not specialized. It includes the organization, website, and
 * breadcrumb list.
 *
 * @author Jacob Mitash
 */
public class DefaultLinkedDataServiceImpl implements LinkedDataService {

    protected String url;

    public DefaultLinkedDataServiceImpl(String url) {
        this.url = url;
    }

    protected JSONArray getLinkedDataJson() throws JSONException {
        JSONArray schemaObjects = new JSONArray();

        schemaObjects.put(LinkedDataUtil.getDefaultOrganization(url));
        schemaObjects.put(LinkedDataUtil.getDefaultWebSite(url));
        schemaObjects.put(LinkedDataUtil.getDefaultBreadcrumbList(url));

        return schemaObjects;
    }

    @Override
    public String getLinkedData() throws JSONException {
        return getLinkedDataJson().toString();
    }
}
