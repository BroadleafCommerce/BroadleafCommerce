package org.broadleafcommerce.core.linked.data;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.codehaus.jettison.json.JSONException;

import java.util.List;

/**
 * Created by jacobmitash on 6/28/17.
 */
public interface CategorySearchLinkedDataService {

    /**
     * Adds linked data to the model for given products
     * @param products the products to get info from
     * @return JSON string of linked data
     */
    String getLinkedData(List<Product> products) throws JSONException;
}
