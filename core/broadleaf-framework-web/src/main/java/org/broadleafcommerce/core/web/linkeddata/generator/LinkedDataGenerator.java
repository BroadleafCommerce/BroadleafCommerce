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
package org.broadleafcommerce.core.web.linkeddata.generator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import javax.servlet.http.HttpServletRequest;

/**
 * This linked data generator provides structured metadata relevant for a page's SEO.
 * <p>
 * See <a href="http://schema.org/" target="_blank">http://schema.org/</a>.
 *
 * @author Jacob Mitash
 * @author Nathan Moore (nathanmoore).
 */
public interface LinkedDataGenerator {

    /**
     * Determines whether or not this LinkedDataGenerator can handle the incoming request.
     * @param request
     * @return whether to handle the destination type
     */
    boolean canHandle(final HttpServletRequest request);

    /**
     * Gets the linked data for default pages
     * @param url
     * @param request
     * @return JSON representation of linked data
     */
    void getLinkedDataJSON(final String url, final HttpServletRequest request, final JSONArray schemaObjects) throws JSONException;

    
    String getStructuredDataContext();
}
