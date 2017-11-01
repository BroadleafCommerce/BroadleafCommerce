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
import org.codehaus.jettison.json.JSONException;

import java.util.List;

/**
 * This linked data service provides metadata relevant to
 * the page.
 *
 * @author Jacob Mitash
 */
public interface LinkedDataService {

    /**
     * Determines whether or not this LinkedDataService can handle the incoming {@link LinkedDataDestinationType}
     * @param destination
     * @return whether or not to handle the destination type
     */
    Boolean canHandle(LinkedDataDestinationType destination);

    /**
     * Gets the linked data for default pages
     * @param url
     * @param products
     * @return string JSON representation of linked data
     */
    String getLinkedData(String url, List<Product> products) throws JSONException;

}
