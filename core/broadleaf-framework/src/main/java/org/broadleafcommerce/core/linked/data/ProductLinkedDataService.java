package org.broadleafcommerce.core.linked.data;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.codehaus.jettison.json.JSONException;

/**
 * Created by jacobmitash on 6/28/17.
 */
public interface ProductLinkedDataService {

    /**
     * Adds linked data to the model for given product
     * @param product the product to get info from
     * @return JSON string of linked data
     */
    String getLinkedData(Product product, String url) throws JSONException;
}
