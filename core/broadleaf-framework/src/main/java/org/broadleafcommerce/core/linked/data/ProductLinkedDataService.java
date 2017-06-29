package org.broadleafcommerce.core.linked.data;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.codehaus.jettison.json.JSONException;

/**
 * Service for getting the linked data for category pages. Implemented in
 * {@link ProductLinkedDataService} which can be extended to modify
 * or add additional metadata.
 *
 * @author Jacob Mitash
 */
public interface ProductLinkedDataService {

    /**
     * Adds linked data to the model for given product
     *
     * @param product the product to get info from
     * @return JSON string of linked data
     */
    String getLinkedData(Product product, String url) throws JSONException;
}
