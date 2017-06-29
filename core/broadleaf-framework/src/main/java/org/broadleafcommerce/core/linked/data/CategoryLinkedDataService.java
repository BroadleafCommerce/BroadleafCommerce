package org.broadleafcommerce.core.linked.data;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.codehaus.jettison.json.JSONException;

import java.util.List;

/**
 * Service for getting the linked data for category pages. Implemented in
 * {@link CategoryLinkedDataServiceImpl} which can be extended to modify
 * or add additional metadata.
 *
 * @author Jacob Mitash
 */
public interface CategoryLinkedDataService {

    /**
     * Adds linked data to the model for given products
     *
     * @param products the products to get info from
     * @param url the URL of the current page
     * @return JSON string of linked data
     */
    String getLinkedData(List<Product> products, String url) throws JSONException;
}
