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
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * This service generates metadata specialized for the homepage, namely the search action. The search action allows
 * search engines to recognize the site has a search feature and allows users to search the site directly from the
 * search engine.
 *
 * @author Jacob Mitash
 */
public class HomepageLinkedDataServiceImpl extends DefaultLinkedDataServiceImpl {

    @Autowired
    protected Environment environment;

    HomepageLinkedDataServiceImpl(String url) {
        super(url);
    }

    @Override
    protected JSONArray getLinkedDataJson() throws JSONException {
        JSONArray schemaObjects = new JSONArray();

        JSONObject webSite = linkedDataUtil.getDefaultWebSite(url);

        JSONObject potentialAction = new JSONObject();
        potentialAction.put("@type", "SearchAction");
        potentialAction.put("target", url.concat(environment.getProperty("site.search")));
        potentialAction.put("query-input", "required name=query");

        webSite.put("potentialAction", potentialAction);

        schemaObjects.put(webSite);
        schemaObjects.put(linkedDataUtil.getDefaultBreadcrumbList(url));
        schemaObjects.put(linkedDataUtil.getDefaultOrganization(url));

        return schemaObjects;
    }
}
